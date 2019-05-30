package com.justinaspetuchovas.health.repository;

import com.justinaspetuchovas.health.model.prescription.PurchaseFact;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.UUID;

/**
 * Repository for {@link PurchaseFact} entities.
 */
public interface PurchaseFactRepository extends JpaRepository<PurchaseFact, UUID> {
  @Query(
      value = "SELECT purchaseFact.purchaseDate AS purchaseDate "
          + "FROM PurchaseFact AS purchaseFact "
          + "JOIN purchaseFact.medicalPrescription AS medicalPrescription "
          + "ON medicalPrescription.id = :medicalPrescriptionId"
  )
  Slice<Date> getPurchaseFacts(
      @Param("medicalPrescriptionId") UUID medicalPrescriptionId,
      Pageable pageable
  );
}
