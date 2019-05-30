package com.justinaspetuchovas.health.model.user.patient;

import com.justinaspetuchovas.health.common.ValidationConstants;
import com.justinaspetuchovas.health.model.record.MedicalRecord;
import com.justinaspetuchovas.health.model.prescription.MedicalPrescription;
import com.justinaspetuchovas.health.model.user.Role;
import com.justinaspetuchovas.health.model.user.User;
import com.justinaspetuchovas.health.model.user.doctor.Doctor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity that represents a patient.
 */
@Entity
@Table(name = "patients", indexes = {
    @Index(
        name = "personal_identification_number_index",
        columnList = "personal_identification_number",
        unique = true
    )
})
public class Patient extends User {
  @Temporal(TemporalType.DATE)
  private Date birthDate;

  @Column(
      name = "personal_identification_number",
      length = ValidationConstants.PERSONAL_IDENTIFICATION_NUMBER_LENGTH
  )
  private String personalIdentificationNumber;

  @ManyToOne
  @JoinColumn(name = "doctor_id")
  private Doctor doctor = null;

  @OneToMany(mappedBy = "patient")
  private Set<MedicalRecord> medicalRecords = new HashSet<>();

  @OneToMany(mappedBy = "patient")
  private Set<MedicalPrescription> medicalPrescriptions = new HashSet<>();

  public Patient() {
  }

  public Patient(
      String username,
      String password,
      String firstName,
      String lastName,
      Date lastPasswordResetDate,
      Date birthDate,
      String personalIdentificationNumber
  ) {
    super(username, password, firstName, lastName, lastPasswordResetDate, Role.PATIENT);
    this.birthDate = birthDate;
    this.personalIdentificationNumber = personalIdentificationNumber;
  }

  public Date getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(Date birthDate) {
    this.birthDate = birthDate;
  }

  public String getPersonalIdentificationNumber() {
    return personalIdentificationNumber;
  }

  public void setPersonalIdentificationNumber(String personalIdentificationNumber) {
    this.personalIdentificationNumber = personalIdentificationNumber;
  }

  public Set<MedicalRecord> getMedicalRecords() {
    return medicalRecords;
  }

  public Set<MedicalPrescription> getMedicalPrescriptions() {
    return medicalPrescriptions;
  }

  public void addMedicalRecord(MedicalRecord medicalRecord) {
    medicalRecords.add(medicalRecord);
  }

  public void addMedicalPrescription(MedicalPrescription medicalPrescription) {
    medicalPrescriptions.add(medicalPrescription);
  }

  public Doctor getDoctor() {
    return doctor;
  }

  public void setDoctor(Doctor doctor) {
    this.doctor = doctor;
  }
}
