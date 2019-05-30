package com.justinaspetuchovas.health.controller;

import com.justinaspetuchovas.health.exception.ForbiddenException;
import com.justinaspetuchovas.health.model.user.Role;
import com.justinaspetuchovas.health.pagination.PageCommand;
import com.justinaspetuchovas.health.pagination.SliceDto;
import com.justinaspetuchovas.health.security.JwtUser;
import com.justinaspetuchovas.health.service.MedicalPrescriptionService;
import com.justinaspetuchovas.health.service.PatientService;
import com.justinaspetuchovas.health.service.PurchaseFactService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

/**
 * Controller that handles requests to perform operations with purchase facts.
 */
@RestController
@RequestMapping(
    "/api/patients/{patientId}/medical-prescriptions/{medicalPrescriptionId}/purchase-facts"
)
public class PurchaseFactController {
  private static final Logger logger = LogManager.getLogger(PurchaseFactController.class);
  private final PurchaseFactService purchaseFactService;
  private final PatientService patientService;
  private final MedicalPrescriptionService medicalPrescriptionService;

  @Autowired
  public PurchaseFactController(
      PurchaseFactService purchaseFactService,
      PatientService patientService,
      MedicalPrescriptionService medicalPrescriptionService
  ) {
    this.purchaseFactService = purchaseFactService;
    this.patientService = patientService;
    this.medicalPrescriptionService = medicalPrescriptionService;
  }

  @PostMapping
  public SliceDto getPurchaseFacts(
      @PathVariable UUID patientId,
      @PathVariable UUID medicalPrescriptionId,
      @Valid @RequestBody PageCommand pageCommand,
      Authentication authentication
  ) {
    JwtUser user = (JwtUser) authentication.getPrincipal();
    UUID userId = user.getId();
    Role userRole = user.getRole();

    if (userRole == Role.PATIENT && !userId.equals(patientId)) {
      logger.warn(
          "Denied access because the patient with id {\"{}\" tried to access another patient's "
              + "with id \"{}\" purchase facts of the medical prescription with id \"{}\".",
          userId,
          patientId,
          medicalPrescriptionId
      );
      throw new ForbiddenException();
    } else if (
        userRole == Role.DOCTOR && !patientService.isPatientAssignedToDoctor(patientId, userId)
    ) {
      logger.warn(
          "Denied access because the doctor tried to access purchase facts of "
              + "the medical prescription with id \"{}\" of the patient with id \"{}\" that is not "
              + "assigned to him.",
          medicalPrescriptionId,
          patientId
      );
      throw new ForbiddenException();
    } else if (
        !medicalPrescriptionService.doesMedicalPrescriptionBelongToPatient(
            medicalPrescriptionId,
            patientId
        )
    ) {
      logger.warn(
          "Denied access because the patient tried to access purchase facts of "
              + "a medical prescription with id \"{}\" that does not belong to him.",
          medicalPrescriptionId
      );
      throw new ForbiddenException();
    } else {
      return purchaseFactService.getPurchaseFacts(medicalPrescriptionId, pageCommand);
    }
  }
}
