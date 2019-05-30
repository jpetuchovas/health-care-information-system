package com.justinaspetuchovas.health.service;

import com.justinaspetuchovas.health.common.TimeProvider;
import com.justinaspetuchovas.health.exception.UserNotFoundException;
import com.justinaspetuchovas.health.model.record.MedicalRecord;
import com.justinaspetuchovas.health.model.record.MedicalRecordProjection;
import com.justinaspetuchovas.health.model.record.NewMedicalRecordCommand;
import com.justinaspetuchovas.health.model.user.doctor.Doctor;
import com.justinaspetuchovas.health.model.user.patient.Patient;
import com.justinaspetuchovas.health.pagination.PageCommand;
import com.justinaspetuchovas.health.pagination.SliceDto;
import com.justinaspetuchovas.health.repository.DoctorRepository;
import com.justinaspetuchovas.health.repository.MedicalRecordRepository;
import com.justinaspetuchovas.health.repository.PatientRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service used to perform operations with medical records.
 */
@Service
public class MedicalRecordService {
  private static final Logger logger = LogManager.getLogger(MedicalRecordService.class);
  private final MedicalRecordRepository medicalRecordRepository;
  private final PatientRepository patientRepository;
  private final DoctorRepository doctorRepository;
  private final TimeProvider timeProvider;

  @Autowired
  public MedicalRecordService(
      MedicalRecordRepository medicalRecordRepository,
      PatientRepository patientRepository,
      DoctorRepository doctorRepository,
      TimeProvider timeProvider
  ) {
    this.medicalRecordRepository = medicalRecordRepository;
    this.patientRepository = patientRepository;
    this.doctorRepository = doctorRepository;
    this.timeProvider = timeProvider;
  }

  @Transactional
  public void createMedicalRecord(
      UUID patientId,
      UUID doctorId,
      NewMedicalRecordCommand newMedicalRecordCommand
  ) {
    Doctor doctor = doctorRepository.findOne(doctorId);
    Patient patient = patientRepository.findOne(patientId);

    if (doctor == null) {
      logger.warn(
          "Could not create a medical record because a doctor with id \"{}\" does not exist.",
          doctorId
      );
      throw new UserNotFoundException("Doctor does not exist.");
    } else if (patient == null) {
      logger.warn(
          "Could not create a medical record because a patient with id \"{}\" does not exist.",
          patientId
      );
      throw new UserNotFoundException("Patient does not exist.");
    } else {
      String diseaseCode = newMedicalRecordCommand.getDiseaseCode();
      MedicalRecord medicalRecord = new MedicalRecord(
          newMedicalRecordCommand.getDescription(),
          newMedicalRecordCommand.getVisitDurationInMinutes(),
          diseaseCode,
          newMedicalRecordCommand.getIsVisitCompensated(),
          newMedicalRecordCommand.getIsVisitRepeated(),
          timeProvider.now(),
          doctor,
          patient
      );

      medicalRecordRepository.save(medicalRecord);
      logger.info(
          "Created a medical record (disease code: \"{}\") for the user with the username \"{}\".",
          diseaseCode,
          patient.getUsername()
      );
    }
  }

  @Transactional(readOnly = true)
  public SliceDto getMedicalRecords(UUID patientId, PageCommand pageCommand) {
    int pageNumber = pageCommand.getPageNumber();
    int pageSize = pageCommand.getPageSize();

    Slice<MedicalRecordProjection> medicalRecordsSlice = medicalRecordRepository.getMedicalRecords(
        patientId,
        new PageRequest(
            pageNumber,
            pageSize,
            Sort.Direction.DESC,
            "date",
            "id"
        )
    );

    logger.info(
        "Returned a page number {} of size {} with a list of the patient's with id \"{}\" medical "
            + "records.",
        pageNumber,
        pageSize,
        patientId
    );

    return new SliceDto(medicalRecordsSlice.getContent(), medicalRecordsSlice.hasNext());
  }
}
