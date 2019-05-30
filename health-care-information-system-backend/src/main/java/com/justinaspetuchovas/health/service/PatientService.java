package com.justinaspetuchovas.health.service;

import com.justinaspetuchovas.health.common.TimeProvider;
import com.justinaspetuchovas.health.exception.FutureDateException;
import com.justinaspetuchovas.health.exception.PersonalIdentificationNumberConflictException;
import com.justinaspetuchovas.health.exception.UsernameConflictException;
import com.justinaspetuchovas.health.filter.PatientFilterForAdministrator;
import com.justinaspetuchovas.health.filter.PatientFilterForDoctor;
import com.justinaspetuchovas.health.filter.PersonalIdentificationNumberFilter;
import com.justinaspetuchovas.health.model.user.patient.NewPatientCommand;
import com.justinaspetuchovas.health.model.user.patient.Patient;
import com.justinaspetuchovas.health.model.user.patient.PatientProjectionForAdministrator;
import com.justinaspetuchovas.health.model.user.patient.PatientProjectionForCsvDownload;
import com.justinaspetuchovas.health.model.user.patient.PatientProjectionForDoctorOrPharmacist;
import com.justinaspetuchovas.health.pagination.SliceDto;
import com.justinaspetuchovas.health.repository.PatientRepository;
import com.justinaspetuchovas.health.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Service used to perform operations with patients.
 */
@Service
public class PatientService {
  private static final Logger logger = LogManager.getLogger(PatientService.class);
  private final UserRepository userRepository;
  private final PatientRepository patientRepository;
  private final PasswordEncoder passwordEncoder;
  private final TimeProvider timeProvider;

  @Autowired
  public PatientService(
      UserRepository userRepository,
      PatientRepository patientRepository,
      PasswordEncoder passwordEncoder,
      TimeProvider timeProvider
  ) {
    this.userRepository = userRepository;
    this.patientRepository = patientRepository;
    this.passwordEncoder = passwordEncoder;
    this.timeProvider = timeProvider;
  }

  /**
   * Registers a new patient.
   *
   * @param newPatientCommand the request to register a new patient
   * @throws UsernameConflictException if a user with the specified username already exists
   * @throws PersonalIdentificationNumberConflictException if a user with the specified personal
   *                                                       identification number already exists
   * @throws FutureDateException if the provided birth date is a future date
   */
  @Transactional
  public void createPatient(NewPatientCommand newPatientCommand) {
    String username = newPatientCommand.getUsername();
    String personalIdentificationNumber = newPatientCommand.getPersonalIdentificationNumber();
    Date birthDate = newPatientCommand.getBirthDate();

    if (userRepository.existsByUsername(username)) {
      logger.info(
          "Could not create a patient with the username \"{}\" because a user with such a username "
              + "already exists.",
          username
      );
      throw new UsernameConflictException();
    } else if (
        patientRepository.existsByPersonalIdentificationNumber(personalIdentificationNumber)
    ) {
      logger.warn(
          "Could not create a patient because a user with the provided personal identification "
              + "number already exists."
      );
      throw new PersonalIdentificationNumberConflictException();
    } else if (birthDate.after(timeProvider.now())) {
      logger.warn(
          "Could not create a patient because the provided birth date \"{}\" is a future date.",
          birthDate
      );
      throw new FutureDateException();
    } else {
      userRepository.save(
          new Patient(
              username,
              passwordEncoder.encode(newPatientCommand.getPassword()),
              newPatientCommand.getFirstName(),
              newPatientCommand.getLastName(),
              timeProvider.now(),
              newPatientCommand.getBirthDate(),
              personalIdentificationNumber
          )
      );

      logger.info("Created a patient with the username \"{}\".", username);
    }
  }

  /**
   * Checks whether a specific patient is assigned to the specified doctor.
   *
   * @param patientId the patient's ID
   * @param doctorId the doctor's ID
   * @return <code>true</code> if the patient is assigned to the specified doctor,
   *         <code>false</code> otherwise
   */
  @Transactional(readOnly = true)
  public boolean isPatientAssignedToDoctor(UUID patientId, UUID doctorId) {
    return patientRepository.isPatientAssignedToDoctor(patientId, doctorId);
  }

  /**
   * Returns information about the requested patient.
   *
   * @param patientId the patient's ID
   * @return information about the requested patient
   */
  @Transactional(readOnly = true)
  public PatientProjectionForDoctorOrPharmacist getPatient(UUID patientId) {
    logger.info("Returned the patient with id \"{}\".", patientId);
    return patientRepository.findById(patientId);
  }

  /**
   * Returns information about the requested patient.
   *
   * @param personalIdentificationNumberFilter filter used to search for patients by a personal
   *                                           identification number
   * @return information about the requested patient
   */
  @Transactional(readOnly = true)
  public PatientProjectionForDoctorOrPharmacist getPatient(
      PersonalIdentificationNumberFilter personalIdentificationNumberFilter
  ) {
    logger.info("Returned a patient by the provided personal identification number.");
    return patientRepository.findByPersonalIdentificationNumber(
        personalIdentificationNumberFilter.getPersonalIdentificationNumber()
    );
  }

  /**
   * Returns a slice of data with information about patients that meet the specified filter's
   * conditions. This method is invoked when a user is logged in as administrator.
   *
   * @param patientFilterForAdministrator the filter used to search for patients
   * @return a slice of data with information about patients
   */
  @Transactional(readOnly = true)
  public SliceDto filterPatientsForAdministrator(
      PatientFilterForAdministrator patientFilterForAdministrator
  ) {
    int pageNumber = patientFilterForAdministrator.getPageNumber();
    int pageSize = patientFilterForAdministrator.getPageSize();
    String firstName = patientFilterForAdministrator.getFirstName();
    String lastName = patientFilterForAdministrator.getLastName();
    String username = patientFilterForAdministrator.getUsername();
    String personalIdentificationNumber =
        patientFilterForAdministrator.getPersonalIdentificationNumber();

    Slice<PatientProjectionForAdministrator> patientsSlice =
        patientRepository.filterPatientsForAdministrator(
            firstName,
            lastName,
            username,
            personalIdentificationNumber,
            new PageRequest(pageNumber, pageSize)
        );

    logger.info(
        "Returned a page number {} of size {} with a list of filtered patients "
            + "(first name: \"{}\"; last name: \"{}\"; username: \"{}\"; "
            + "personal identification number: not logged).",
        pageNumber,
        pageSize,
        firstName,
        lastName,
        username
    );

    return new SliceDto(patientsSlice.getContent(), patientsSlice.hasNext());
  }

  /**
   * Returns a slice of data with information about patients that meet the specified filter's
   * conditions and are assigned to the specified doctor. This method is invoked when a user
   * is logged in a doctor.
   *
   * @param doctorId the doctor's, whose patients to search for, ID
   * @param patientFilterForDoctor the filter used to search for patients
   * @return a slice of data with information about patients
   */
  @Transactional(readOnly = true)
  public SliceDto filterPatientsForDoctor(
      UUID doctorId,
      PatientFilterForDoctor patientFilterForDoctor
  ) {
    int pageNumber = patientFilterForDoctor.getPageNumber();
    int pageSize = patientFilterForDoctor.getPageSize();
    String firstName = patientFilterForDoctor.getFirstName();
    String lastName = patientFilterForDoctor.getLastName();
    String personalIdentificationNumber = patientFilterForDoctor.getPersonalIdentificationNumber();
    String diseaseCode = patientFilterForDoctor.getDiseaseCode();
    Pageable pageable = new PageRequest(pageNumber, pageSize);

    Slice<PatientProjectionForDoctorOrPharmacist> filteredPatientsSlice = diseaseCode.isEmpty()
        ? patientRepository.filterPatientsForDoctorWithoutDiseaseCode(
            doctorId,
            firstName,
            lastName,
            personalIdentificationNumber,
            pageable
        )
        : patientRepository.filterPatientsForDoctorWithDiseaseCode(
            doctorId,
            firstName,
            lastName,
            personalIdentificationNumber,
            diseaseCode,
            pageable
        );

    logger.info(
        "Returned a page number {} of size {} with a list of filtered patients "
            + "(first name: \"{}\"; last name: \"{}\"; disease code: \"{}\"; "
            + "personal identification number: not logged).",
        pageNumber,
        pageSize,
        firstName,
        lastName,
        diseaseCode
    );

    return new SliceDto(filteredPatientsSlice.getContent(), filteredPatientsSlice.hasNext());
  }

  /**
   * Returns a list of partial views of patients assigned to a specific doctor.
   *
   * @param doctorId the doctor's ID for whom to find all his patients
   * @return a list of partial view of patients
   */
  @Transactional(readOnly = true)
  public List<PatientProjectionForCsvDownload> getDoctorsPatients(UUID doctorId) {
    logger.info("Returned all patients assigned to the doctor.");
    return patientRepository.getDoctorsPatients(doctorId);
  }
}
