package com.justinaspetuchovas.health.controller;

import com.justinaspetuchovas.health.exception.ForbiddenException;
import com.justinaspetuchovas.health.model.prescription.NewMedicalPrescriptionCommand;
import com.justinaspetuchovas.health.model.user.Role;
import com.justinaspetuchovas.health.pagination.PageCommand;
import com.justinaspetuchovas.health.pagination.SliceDto;
import com.justinaspetuchovas.health.security.JwtUser;
import com.justinaspetuchovas.health.service.MedicalPrescriptionService;
import com.justinaspetuchovas.health.service.PatientService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Set;
import java.util.UUID;

/**
 * Controller that handles requests to perform operations with medical prescriptions.
 */
@RestController
@RequestMapping("/api/patients/{patientId}/medical-prescriptions")
public class MedicalPrescriptionController {
  private static final Logger logger = LogManager.getLogger(MedicalPrescriptionController.class);
  private final MedicalPrescriptionService medicalPrescriptionService;
  private final PatientService patientService;

  @Autowired
  public MedicalPrescriptionController(
      MedicalPrescriptionService medicalPrescriptionService,
      PatientService patientService
  ) {
    this.medicalPrescriptionService = medicalPrescriptionService;
    this.patientService = patientService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public void createMedicalPrescription(
      @PathVariable UUID patientId,
      @Valid @RequestBody NewMedicalPrescriptionCommand newMedicalPrescriptionCommand,
      Authentication authentication
  ) {
    UUID doctorId = ((JwtUser) authentication.getPrincipal()).getId();

    medicalPrescriptionService.createMedicalPrescription(
        patientId,
        doctorId,
        newMedicalPrescriptionCommand
    );
  }

  @PostMapping("/page")
  public SliceDto getMedicalPrescriptions(
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
              + "with id \"{}\" medical prescriptions.",
          userId,
          patientId
      );
      throw new ForbiddenException();
    } else if (
        userRole == Role.DOCTOR && !patientService.isPatientAssignedToDoctor(patientId, userId)
    ) {
      logger.warn(
          "Denied access because the doctor tried to access the medical prescriptions of "
              + "the patient with id \"{}\" that is not assigned to him.",
          patientId
      );
      throw new ForbiddenException();
    } else {
      return medicalPrescriptionService.getMedicalPrescriptions(patientId, pageCommand);
    }
  }

  @PostMapping("/valid")
  public SliceDto getValidMedicalPrescriptions(
      @PathVariable UUID patientId,
      @Valid @RequestBody PageCommand pageCommand
  ) {
    return medicalPrescriptionService.getValidMedicalPrescriptions(patientId, pageCommand);
  }

  @PutMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void markMedicalPrescriptionsAsUsed(
      @PathVariable UUID patientId,
      @RequestBody Set<UUID> medicalPrescriptionIds,
      Authentication authentication
  ) {
    UUID pharmacistId = ((JwtUser) authentication.getPrincipal()).getId();

    medicalPrescriptionService.markMedicalPrescriptionsAsUsed(
        patientId,
        pharmacistId,
        medicalPrescriptionIds
    );
  }
}
