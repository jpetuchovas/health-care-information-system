package com.justinaspetuchovas.health.repository;

import com.justinaspetuchovas.health.model.prescription.MedicalPrescription;
import com.justinaspetuchovas.health.model.prescription.MedicalPrescriptionProjection;
import com.justinaspetuchovas.health.model.prescription.MedicalPrescriptionProjectionWithPurchaseFactCount;
import com.justinaspetuchovas.health.statistics.ActiveIngredientUsageCountProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Repository for {@link MedicalPrescription} entities.
 */
public interface MedicalPrescriptionRepository extends JpaRepository<MedicalPrescription, UUID> {
  @Query(
      "SELECT CASE WHEN COUNT(medicalPrescription.id) = 1 THEN TRUE ELSE FALSE END "
          + "FROM MedicalPrescription AS medicalPrescription "
          + "JOIN medicalPrescription.patient AS patient ON patient.id = :patientId "
          + "WHERE medicalPrescription.id = :medicalPrescriptionId"
  )
  boolean doesMedicalPrescriptionBelongToPatient(
      @Param("medicalPrescriptionId") UUID medicalPrescriptionId,
      @Param("patientId") UUID patientId
  );

  @Query(
      "SELECT medicalPrescription.id AS id, "
          + "medicalPrescription.activeIngredient AS activeIngredient, "
          + "medicalPrescription.activeIngredientQuantity AS activeIngredientQuantity, "
          + "medicalPrescription.activeIngredientMeasurementUnit "
          + "AS activeIngredientMeasurementUnit, "
          + "medicalPrescription.usageDescription AS usageDescription, "
          + "medicalPrescription.issueDate AS issueDate, "
          + "medicalPrescription.hasUnlimitedValidity AS hasUnlimitedValidity, "
          + "medicalPrescription.validityEndDate AS validityEndDate, "
          + "CONCAT(doctor.firstName, ' ', doctor.lastName) AS doctorFullName, "
          + "COUNT(purchaseFacts.id) AS purchaseFactCount "
          + "FROM MedicalPrescription AS medicalPrescription "
          + "JOIN medicalPrescription.patient AS patient ON patient.id = :patientId "
          + "JOIN medicalPrescription.doctor AS doctor "
          + "LEFT JOIN medicalPrescription.purchaseFacts AS purchaseFacts "
          + "GROUP BY medicalPrescription.id"
  )
  Slice<MedicalPrescriptionProjectionWithPurchaseFactCount> getMedicalPrescriptions(
      @Param("patientId") UUID patientId,
      Pageable pageable
  );

  @Query(
      "SELECT medicalPrescription.id AS id, "
          + "medicalPrescription.activeIngredient AS activeIngredient, "
          + "medicalPrescription.activeIngredientQuantity AS activeIngredientQuantity, "
          + "medicalPrescription.activeIngredientMeasurementUnit "
          + "AS activeIngredientMeasurementUnit, "
          + "medicalPrescription.usageDescription AS usageDescription, "
          + "medicalPrescription.issueDate AS issueDate, "
          + "medicalPrescription.hasUnlimitedValidity AS hasUnlimitedValidity, "
          + "medicalPrescription.validityEndDate AS validityEndDate, "
          + "CONCAT(doctor.firstName, ' ', doctor.lastName) AS doctorFullName "
          + "FROM MedicalPrescription AS medicalPrescription "
          + "JOIN medicalPrescription.patient AS patient ON patient.id = :patientId "
          + "JOIN medicalPrescription.doctor AS doctor "
          + "WHERE medicalPrescription.hasUnlimitedValidity = TRUE "
          + "OR medicalPrescription.validityEndDate >= :currentDate"
  )
  Slice<MedicalPrescriptionProjection> getValidMedicalPrescriptions(
      @Param("patientId") UUID patientId,
      @Param("currentDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date currentDate,
      Pageable pageable
  );

  @Query(
      "SELECT medicalPrescription.activeIngredient AS activeIngredient, "
          + "COUNT(medicalPrescription.id) AS usageCount "
          + "FROM MedicalPrescription AS medicalPrescription "
          + "JOIN medicalPrescription.purchaseFacts AS purchaseFacts "
          + "GROUP BY medicalPrescription.activeIngredient "
          + "ORDER BY COUNT(medicalPrescription.id) DESC"
  )
  List<ActiveIngredientUsageCountProjection> getMostOftenUsedActiveIngredientCounts(
      Pageable pageable
  );
}
