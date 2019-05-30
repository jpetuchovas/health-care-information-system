package com.justinaspetuchovas.health.service;

import com.justinaspetuchovas.health.common.TimeProvider;
import com.justinaspetuchovas.health.exception.UserNotFoundException;
import com.justinaspetuchovas.health.exception.UsernameConflictException;
import com.justinaspetuchovas.health.filter.DoctorFilter;
import com.justinaspetuchovas.health.model.user.doctor.DoctorProjection;
import com.justinaspetuchovas.health.model.user.doctor.Doctor;
import com.justinaspetuchovas.health.model.user.doctor.NewDoctorCommand;
import com.justinaspetuchovas.health.model.user.patient.Patient;
import com.justinaspetuchovas.health.pagination.SliceDto;
import com.justinaspetuchovas.health.repository.DoctorRepository;
import com.justinaspetuchovas.health.repository.PatientRepository;
import com.justinaspetuchovas.health.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service used to perform operations with doctors.
 */
@Service
public class DoctorService {
  private static final Logger logger = LogManager.getLogger(DoctorService.class);
  private final UserRepository userRepository;
  private final DoctorRepository doctorRepository;
  private final PatientRepository patientRepository;
  private final PasswordEncoder passwordEncoder;
  private final TimeProvider timeProvider;

  @Autowired
  public DoctorService(
      UserRepository userRepository,
      DoctorRepository doctorRepository,
      PatientRepository patientRepository,
      PasswordEncoder passwordEncoder,
      TimeProvider timeProvider
  ) {
    this.userRepository = userRepository;
    this.doctorRepository = doctorRepository;
    this.patientRepository = patientRepository;
    this.passwordEncoder = passwordEncoder;
    this.timeProvider = timeProvider;
  }

  @Transactional
  public void createDoctor(NewDoctorCommand newDoctorCommand) {
    String username = newDoctorCommand.getUsername();

    if (userRepository.existsByUsername(username)) {
      logger.info(
          "Could not create a doctor with the username \"{}\" because a user with such a username "
              + "already exists.",
          username
      );
      throw new UsernameConflictException();
    } else {
      doctorRepository.save(
          new Doctor(
              username,
              passwordEncoder.encode(newDoctorCommand.getPassword()),
              newDoctorCommand.getFirstName(),
              newDoctorCommand.getLastName(),
              timeProvider.now(),
              newDoctorCommand.getSpecialization()
          )
      );

      logger.info("Created a doctor with the username \"{}\".", username);
    }
  }

  @Transactional(readOnly = true)
  public SliceDto filterDoctors(DoctorFilter doctorFilter) {
    String firstName = doctorFilter.getFirstName();
    String lastName = doctorFilter.getLastName();
    String username = doctorFilter.getUsername();
    String specialization = doctorFilter.getSpecialization();
    int pageNumber = doctorFilter.getPageNumber();
    int pageSize = doctorFilter.getPageSize();

    Slice<DoctorProjection> doctorsSlice = doctorRepository.filterDoctors(
        firstName,
        lastName,
        username,
        specialization,
        new PageRequest(pageNumber, pageSize)
    );

    logger.info(
        "Returned a page number {} of size {} with a list of filtered doctors "
            + "(first name: \"{}\"; last name: \"{}\"; username: \"{}\"; specialization: \"{}\").",
        pageNumber,
        pageSize,
        firstName,
        lastName,
        username,
        specialization
    );

    return new SliceDto(doctorsSlice.getContent(), doctorsSlice.hasNext());
  }

  @Transactional
  public void assignPatient(UUID doctorId, UUID patientId) {
    Doctor doctor = doctorRepository.findOne(doctorId);
    Patient patient = patientRepository.findOne(patientId);

    if (doctor == null) {
      logger.warn(
          "Could not assign a patient to a doctor because a doctor with id \"{}\" does not exist.",
          doctorId
      );
      throw new UserNotFoundException("Doctor does not exist.");
    } else if (patient == null) {
      logger.warn(
          "Could not assign a patient to a doctor because a patient with id \"{}\" does not exist.",
          patientId
      );
      throw new UserNotFoundException("Patient does not exist.");
    } else {
      patient.setDoctor(doctor);
      logger.info(
          "Assigned the patient with the username \"{}\" to the doctor with the username \"{}\".",
          patient.getUsername(),
          doctor.getUsername()
      );
    }
  }
}
