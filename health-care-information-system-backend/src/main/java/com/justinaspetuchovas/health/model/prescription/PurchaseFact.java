package com.justinaspetuchovas.health.model.prescription;

import com.justinaspetuchovas.health.model.user.pharmacist.Pharmacist;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * Purchase fact that indicates that a particular medical prescription was used to purchase
 * a medication from a particular pharmacist. Includes information about the purchase date.
 */
@Entity
@Table(name = "purchase_facts")
public class PurchaseFact {
  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @Column(name = "id", columnDefinition = "UUID(16)", updatable = false, nullable = false)
  private UUID id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "pharmacist_id", nullable = false)
  private Pharmacist pharmacist;

  @ManyToOne
  @JoinColumn(name = "medical_prescription_id", nullable = false)
  private MedicalPrescription medicalPrescription;

  @Temporal(TemporalType.DATE)
  private Date purchaseDate;

  public PurchaseFact() {
  }

  public PurchaseFact(
      Pharmacist pharmacist,
      MedicalPrescription medicalPrescription,
      Date purchaseDate
  ) {
    this.pharmacist = pharmacist;
    this.medicalPrescription = medicalPrescription;
    this.purchaseDate = purchaseDate;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Pharmacist getPharmacist() {
    return pharmacist;
  }

  public void setPharmacist(Pharmacist pharmacist) {
    this.pharmacist = pharmacist;
  }

  public MedicalPrescription getMedicalPrescription() {
    return medicalPrescription;
  }

  public void setMedicalPrescription(MedicalPrescription medicalPrescription) {
    this.medicalPrescription = medicalPrescription;
  }

  public Date getPurchaseDate() {
    return purchaseDate;
  }

  public void setPurchaseDate(Date purchaseDate) {
    this.purchaseDate = purchaseDate;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }

    if (object == null || getClass() != object.getClass()) {
      return false;
    }

    PurchaseFact that = (PurchaseFact) object;
    return Objects.equals(pharmacist, that.pharmacist)
        && Objects.equals(medicalPrescription, that.medicalPrescription)
        && Objects.equals(purchaseDate, that.purchaseDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pharmacist, medicalPrescription, purchaseDate);
  }
}
