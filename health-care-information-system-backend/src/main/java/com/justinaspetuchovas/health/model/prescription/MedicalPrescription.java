package com.justinaspetuchovas.health.model.prescription;

import com.justinaspetuchovas.health.common.ValidationConstants;
import com.justinaspetuchovas.health.model.user.doctor.Doctor;
import com.justinaspetuchovas.health.model.user.patient.Patient;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Entity that represents a medical prescription.
 */
@Entity
@Table(name = "medical_prescriptions", indexes = {
    @Index(name = "active_ingredient_index", columnList = "active_ingredient"),
    @Index(name = "validity_end_date_index", columnList = "validity_end_date")
})
public class MedicalPrescription {
  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @Column(name = "id", columnDefinition = "UUID(16)", updatable = false, nullable = false)
  private UUID id;

  @Column(name = "active_ingredient", length = ValidationConstants.ACTIVE_INGREDIENT_LENGTH_MAX)
  private String activeIngredient;

  @Column(
      precision = ValidationConstants.ACTIVE_INGREDIENT_QUANTITY_PRECISION,
      scale = ValidationConstants.ACTIVE_INGREDIENT_QUANTITY_SCALE
  )
  // BigDecimal is used to prevent rounding errors.
  private BigDecimal activeIngredientQuantity;

  @Column(length = ValidationConstants.ACTIVE_INGREDIENT_MEASUREMENT_UNIT_LENGTH_MAX)
  private ActiveIngredientMeasurementUnit activeIngredientMeasurementUnit;

  @Column(length = ValidationConstants.DESCRIPTION_LENGTH_MAX)
  private String usageDescription;

  @Temporal(TemporalType.DATE)
  private Date issueDate;

  private boolean hasUnlimitedValidity;

  @Temporal(TemporalType.DATE)
  @Column(name = "validity_end_date")
  private Date validityEndDate;

  @OneToMany(mappedBy = "medicalPrescription")
  private Set<PurchaseFact> purchaseFacts = new HashSet<>();

  @ManyToOne(optional = false)
  @JoinColumn(name = "doctor_id", nullable = false)
  private Doctor doctor;

  @ManyToOne(optional = false)
  @JoinColumn(name = "patient_id", nullable = false)
  private Patient patient;

  public MedicalPrescription() {
  }

  public MedicalPrescription(
      String activeIngredient,
      BigDecimal activeIngredientQuantity,
      ActiveIngredientMeasurementUnit activeIngredientMeasurementUnit,
      String usageDescription,
      Date issueDate,
      boolean hasUnlimitedValidity,
      Date validityEndDate,
      Doctor doctor,
      Patient patient
  ) {
    if (hasUnlimitedValidity && validityEndDate != null) {
      throw new IllegalArgumentException(
          "Should call constructor without providing validity end date or setting it to null "
              + "when creating a medical prescription with unlimited validity end date."
      );
    }

    if (!hasUnlimitedValidity && validityEndDate == null) {
      throw new IllegalArgumentException(
          "A validity end date must not be null when creating a medical prescription with limited "
              + "validity."
      );
    }

    this.activeIngredient = activeIngredient;
    this.activeIngredientQuantity = activeIngredientQuantity;
    this.activeIngredientMeasurementUnit = activeIngredientMeasurementUnit;
    this.usageDescription = usageDescription;
    this.issueDate = issueDate;
    this.hasUnlimitedValidity = hasUnlimitedValidity;
    this.validityEndDate = validityEndDate;
    this.doctor = doctor;
    this.patient = patient;
  }

  public MedicalPrescription(
      String activeIngredient,
      BigDecimal activeIngredientQuantity,
      ActiveIngredientMeasurementUnit activeIngredientMeasurementUnit,
      String usageDescription,
      Date issueDate,
      boolean hasUnlimitedValidity,
      Doctor doctor,
      Patient patient
  ) {
    if (!hasUnlimitedValidity) {
      throw new IllegalArgumentException(
          "Must provide a validity end date when creating a medical prescription with unlimited "
              + "validity."
      );
    }

    this.activeIngredient = activeIngredient;
    this.activeIngredientQuantity = activeIngredientQuantity;
    this.activeIngredientMeasurementUnit = activeIngredientMeasurementUnit;
    this.usageDescription = usageDescription;
    this.issueDate = issueDate;
    this.hasUnlimitedValidity = true;
    this.validityEndDate = null;
    this.doctor = doctor;
    this.patient = patient;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getActiveIngredient() {
    return activeIngredient;
  }

  public void setActiveIngredient(String activeIngredient) {
    this.activeIngredient = activeIngredient;
  }

  public BigDecimal getActiveIngredientQuantity() {
    return activeIngredientQuantity;
  }

  public void setActiveIngredientQuantity(BigDecimal activeIngredientQuantity) {
    this.activeIngredientQuantity = activeIngredientQuantity;
  }

  public ActiveIngredientMeasurementUnit getActiveIngredientMeasurementUnit() {
    return activeIngredientMeasurementUnit;
  }

  public void setActiveIngredientMeasurementUnit(
      String activeIngredientMeasurementUnit
  ) {
    this.activeIngredientMeasurementUnit = ActiveIngredientMeasurementUnit.valueOf(
        activeIngredientMeasurementUnit.toUpperCase()
    );
  }

  public String getUsageDescription() {
    return usageDescription;
  }

  public void setUsageDescription(String usageDescription) {
    this.usageDescription = usageDescription;
  }

  public Date getIssueDate() {
    return issueDate;
  }

  public void setIssueDate(Date issueDate) {
    this.issueDate = issueDate;
  }

  public boolean getHasUnlimitedValidity() {
    return hasUnlimitedValidity;
  }

  public void setHasUnlimitedValidity(boolean hasUnlimitedValidity) {
    this.hasUnlimitedValidity = hasUnlimitedValidity;
  }

  public Date getValidityEndDate() {
    return validityEndDate;
  }

  public void setValidityEndDate(Date validityEndDate) {
    this.validityEndDate = validityEndDate;
  }

  public Set<PurchaseFact> getPurchaseFacts() {
    return purchaseFacts;
  }

  public void setPurchaseFacts(Set<PurchaseFact> purchaseFacts) {
    this.purchaseFacts = purchaseFacts;
  }

  public void addPurchaseFact(PurchaseFact purchaseFact) {
    purchaseFacts.add(purchaseFact);
  }

  public Doctor getDoctor() {
    return doctor;
  }

  public void setDoctor(Doctor doctor) {
    this.doctor = doctor;
  }

  public Patient getPatient() {
    return patient;
  }

  public void setPatient(Patient patient) {
    this.patient = patient;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }

    if (object == null || getClass() != object.getClass()) {
      return false;
    }

    MedicalPrescription that = (MedicalPrescription) object;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
