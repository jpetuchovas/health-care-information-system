package com.justinaspetuchovas.health.model.record;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * Entity that represents a medical record.
 */
@Entity
@Table(name = "medical_records", indexes = {
    @Index(name = "disease_code_index", columnList = "disease_code"),
    @Index(name = "date_index", columnList = "date")
})
public class MedicalRecord {
  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @Column(name = "id", columnDefinition = "UUID(16)", updatable = false, nullable = false)
  private UUID id;

  @Column(length = ValidationConstants.DESCRIPTION_LENGTH_MAX)
  private String description;

  private short visitDurationInMinutes;

  @Column(
      name = "disease_code",
      columnDefinition = "VARCHAR_IGNORECASE(" + ValidationConstants.DISEASE_CODE_LENGTH_MAX + ")"
  )
  private String diseaseCode;

  private boolean isVisitCompensated;
  private boolean isVisitRepeated;

  @Temporal(TemporalType.DATE)
  private Date date;

  @ManyToOne(optional = false)
  @JoinColumn(name = "doctor_id", nullable = false)
  private Doctor doctor;

  @ManyToOne(optional = false)
  @JoinColumn(name = "patient_id", nullable = false)
  private Patient patient;

  public MedicalRecord() {
  }

  public MedicalRecord(
      String description,
      short visitDurationInMinutes,
      String diseaseCode,
      boolean isVisitCompensated,
      boolean isVisitRepeated,
      Date date,
      Doctor doctor,
      Patient patient
  ) {
    this.description = description;
    this.visitDurationInMinutes = visitDurationInMinutes;
    this.diseaseCode = diseaseCode;
    this.isVisitCompensated = isVisitCompensated;
    this.isVisitRepeated = isVisitRepeated;
    this.date = date;
    this.doctor = doctor;
    this.patient = patient;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public short getVisitDurationInMinutes() {
    return visitDurationInMinutes;
  }

  public void setVisitDurationInMinutes(short visitDurationInMinutes) {
    this.visitDurationInMinutes = visitDurationInMinutes;
  }

  public String getDiseaseCode() {
    return diseaseCode;
  }

  public void setDiseaseCode(String diseaseCode) {
    this.diseaseCode = diseaseCode;
  }

  public boolean getIsVisitCompensated() {
    return isVisitCompensated;
  }

  public void setIsVisitCompensated(boolean visitCompensated) {
    isVisitCompensated = visitCompensated;
  }

  public boolean getIsVisitRepeated() {
    return isVisitRepeated;
  }

  public void setIsVisitRepeated(boolean visitRepeated) {
    isVisitRepeated = visitRepeated;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
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

    MedicalRecord that = (MedicalRecord) object;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
