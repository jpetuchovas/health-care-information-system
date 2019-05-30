package com.justinaspetuchovas.health.controller;

import com.justinaspetuchovas.health.exception.ForbiddenException;
import com.justinaspetuchovas.health.model.record.NewMedicalRecordCommand;
import com.justinaspetuchovas.health.model.user.Role;
import com.justinaspetuchovas.health.pagination.PageCommand;
import com.justinaspetuchovas.health.pagination.SliceDto;
import com.justinaspetuchovas.health.security.JwtUser;
import com.justinaspetuchovas.health.service.MedicalRecordService;
import com.justinaspetuchovas.health.service.PatientService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

/**
 * Controller that handles requests to perform operations with medical records.
 */
@RestController
@RequestMapping("/api/patients/{patientId}/medical-records")
public class MedicalRecordController {
  private static final Logger logger = LogManager.getLogger(MedicalRecordController.class);
  private final PatientService patientService;
  private final MedicalRecordService medicalRecordService;

  @Autowired
  public MedicalRecordController(
      PatientService patientService,
      MedicalRecordService medicalRecordService
  ) {
    this.patientService = patientService;
    this.medicalRecordService = medicalRecordService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public void createMedicalRecord(
      @PathVariable UUID patientId,
      @Valid @RequestBody NewMedicalRecordCommand newMedicalRecordCommand,
      Authentication authentication
  ) {
    UUID doctorId = ((JwtUser) authentication.getPrincipal()).getId();
    medicalRecordService.createMedicalRecord(patientId, doctorId, newMedicalRecordCommand);
  }

  @PostMapping("/page")
  public SliceDto getMedicalRecords(
      @PathVariable UUID patientId,
      @Valid @RequestBody PageCommand pageCommand,
      Authentication authentication
  ) {
    JwtUser user = (JwtUser) authentication.getPrincipal();
    UUID userId = user.getId();
    Role userRole = user.getRole();

    if (userRole == Role.PATIENT && !userId.equals(patientId)) {
      logger.warn(
          "Denied access because the patient with id \"{}\" tried to access another patient's "
              + "with id \"{}\" medical records.",
          userId,
          patientId
      );
      throw new ForbiddenException();
    } else if (
        userRole == Role.DOCTOR && !patientService.isPatientAssignedToDoctor(patientId, userId)
    ) {
      logger.warn(
          "Denied access because the doctor tried to access the medical records of "
              + "the patient with id \"{}\" that is not assigned to him.",
          patientId
      );
      throw new ForbiddenException();
    } else {
      return medicalRecordService.getMedicalRecords(patientId, pageCommand);
    }
  }
}
