package com.justinaspetuchovas.health.model.user.doctor;

import com.justinaspetuchovas.health.common.ValidationConstants;
import com.justinaspetuchovas.health.model.prescription.MedicalPrescription;
import com.justinaspetuchovas.health.model.record.MedicalRecord;
import com.justinaspetuchovas.health.model.user.Role;
import com.justinaspetuchovas.health.model.user.User;
import com.justinaspetuchovas.health.model.user.patient.Patient;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity that represents a doctor.
 */
@Entity
@Table(name = "doctors", indexes = {
    @Index(name = "specialization_index", columnList = "specialization")
})
public class Doctor extends User {
  @Column(
      columnDefinition = "VARCHAR_IGNORECASE(" + ValidationConstants.SPECIALIZATION_LENGTH_MAX + ")"
  )
  private String specialization;

  @OneToMany(mappedBy = "doctor")
  private Set<Patient> patients = new HashSet<>();

  @OneToMany(mappedBy = "doctor")
  private Set<MedicalRecord> medicalRecords = new HashSet<>();

  @OneToMany(mappedBy = "doctor")
  private Set<MedicalPrescription> medicalPrescriptions = new HashSet<>();

  public Doctor() {
  }

  public Doctor(
      String username,
      String password,
      String firstName,
      String lastName,
      Date lastPasswordResetDate,
      String specialization
  ) {
    super(username, password, firstName, lastName, lastPasswordResetDate, Role.DOCTOR);
    this.specialization = specialization;
  }

  public String getSpecialization() {
    return specialization;
  }

  public void setSpecialization(String specialization) {
    this.specialization = specialization;
  }

  public Set<Patient> getPatients() {
    return patients;
  }

  public Set<MedicalRecord> getMedicalRecords() {
    return medicalRecords;
  }

  public Set<MedicalPrescription> getMedicalPrescriptions() {
    return medicalPrescriptions;
  }

  public void addPatient(Patient patient) {
    patients.add(patient);
  }

  public void addMedicalRecord(MedicalRecord medicalRecord) {
    medicalRecords.add(medicalRecord);
  }

  public void addMedicalPrescription(MedicalPrescription medicalPrescription) {
    medicalPrescriptions.add(medicalPrescription);
  }
}
