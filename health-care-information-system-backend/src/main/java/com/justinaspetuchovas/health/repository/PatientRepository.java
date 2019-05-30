package com.justinaspetuchovas.health.repository;

import com.justinaspetuchovas.health.model.user.patient.Patient;
import com.justinaspetuchovas.health.model.user.patient.PatientProjectionForAdministrator;
import com.justinaspetuchovas.health.model.user.patient.PatientProjectionForCsvDownload;
import com.justinaspetuchovas.health.model.user.patient.PatientProjectionForDoctorOrPharmacist;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * Repository for {@link Patient} entities.
 */
public interface PatientRepository extends JpaRepository<Patient, UUID> {
  PatientProjectionForDoctorOrPharmacist findById(UUID id);

  @Query(
      "SELECT CASE WHEN COUNT(patient.id) > 0 THEN TRUE ELSE FALSE END "
          + "FROM Patient AS patient "
          + "WHERE patient.personalIdentificationNumber = :personalIdentificationNumber"
  )
  boolean existsByPersonalIdentificationNumber(
      @Param("personalIdentificationNumber") String personalIdentificationNumber
  );

  @Query(
      "SELECT CASE WHEN COUNT(patient.id) = 1 THEN TRUE ELSE FALSE END "
          + "FROM Patient AS patient "
          + "JOIN patient.doctor AS doctor ON doctor.id = :doctorId "
          + "WHERE patient.id = :patientId"
  )
  boolean isPatientAssignedToDoctor(
      @Param("patientId") UUID patientId,
      @Param("doctorId") UUID doctorId
  );

  PatientProjectionForDoctorOrPharmacist findByPersonalIdentificationNumber(
      String personalIdentificationNumber
  );

  @Query(
      "SELECT patient.id AS id, "
          + "patient.firstName AS firstName, "
          + "patient.lastName AS lastName, "
          + "patient.username AS username, "
          + "patient.personalIdentificationNumber AS personalIdentificationNumber "
          + "FROM Patient AS patient "
          + "WHERE patient.firstName LIKE :firstName "
          + "AND patient.lastName LIKE :lastName "
          + "AND patient.username LIKE :username "
          + "AND patient.personalIdentificationNumber LIKE :personalIdentificationNumber"
  )
  Slice<PatientProjectionForAdministrator> filterPatientsForAdministratorByStartPattern(
      @Param("firstName") String firstName,
      @Param("lastName") String lastName,
      @Param("username") String username,
      @Param("personalIdentificationNumber") String personalIdentificationNumber,
      Pageable pageable
  );

  default Slice<PatientProjectionForAdministrator> filterPatientsForAdministrator(
      String firstName,
      String lastName,
      String username,
      String personalIdentificationNumber,
      Pageable pageable
  ) {
    return filterPatientsForAdministratorByStartPattern(
        firstName + "%",
        lastName + "%",
        username + "%",
        personalIdentificationNumber + "%",
        pageable
    );
  }

  @Query(
      "SELECT DISTINCT patient.id AS id, "
          + "patient.firstName AS firstName, "
          + "patient.lastName AS lastName, "
          + "patient.personalIdentificationNumber AS personalIdentificationNumber, "
          + "patient.birthDate AS birthDate "
          + "FROM Patient AS patient "
          + "JOIN patient.doctor AS doctor ON doctor.id = :doctorId "
          + "JOIN patient.medicalRecords AS medicalRecords "
          + "ON medicalRecords.diseaseCode LIKE :diseaseCode "
          + "WHERE patient.firstName LIKE :firstName "
          + "AND patient.lastName LIKE :lastName "
          + "AND patient.personalIdentificationNumber LIKE :personalIdentificationNumber"
  )
  Slice<PatientProjectionForDoctorOrPharmacist>
      filterPatientsForDoctorWithDiseaseCodeByStartPattern(
          @Param("doctorId") UUID doctorId,
          @Param("firstName") String firstName,
          @Param("lastName") String lastName,
          @Param("personalIdentificationNumber") String personalIdentificationNumber,
          @Param("diseaseCode") String diseaseCode,
          Pageable pageable
      );

  default Slice<PatientProjectionForDoctorOrPharmacist> filterPatientsForDoctorWithDiseaseCode(
      UUID doctorId,
      String firstName,
      String lastName,
      String personalIdentificationNumber,
      String diseaseCode,
      Pageable pageable
  ) {
    return filterPatientsForDoctorWithDiseaseCodeByStartPattern(
        doctorId,
        firstName + "%",
        lastName + "%",
        personalIdentificationNumber + "%",
        diseaseCode + "%",
        pageable
    );
  }

  @Query(
      "SELECT patient.id AS id, "
          + "patient.firstName AS firstName, "
          + "patient.lastName AS lastName, "
          + "patient.personalIdentificationNumber AS personalIdentificationNumber, "
          + "patient.birthDate AS birthDate "
          + "FROM Patient AS patient "
          + "JOIN patient.doctor AS doctor ON doctor.id = :doctorId "
          + "WHERE patient.firstName LIKE :firstName "
          + "AND patient.lastName LIKE :lastName "
          + "AND patient.personalIdentificationNumber LIKE :personalIdentificationNumber"
  )
  Slice<PatientProjectionForDoctorOrPharmacist>
      filterPatientsForDoctorWithoutDiseaseCodeByStartPattern(
          @Param("doctorId") UUID doctorId,
          @Param("firstName") String firstName,
          @Param("lastName") String lastName,
          @Param("personalIdentificationNumber") String personalIdentificationNumber,
          Pageable pageable
      );

  default Slice<PatientProjectionForDoctorOrPharmacist> filterPatientsForDoctorWithoutDiseaseCode(
      UUID doctorId,
      String firstName,
      String lastName,
      String personalIdentificationNumber,
      Pageable pageable
  ) {
    return filterPatientsForDoctorWithoutDiseaseCodeByStartPattern(
        doctorId,
        firstName + "%",
        lastName + "%",
        personalIdentificationNumber + "%",
        pageable
    );
  }

  @Query(
      "SELECT patient.firstName AS firstName, "
          + "patient.lastName AS lastName, "
          + "patient.personalIdentificationNumber AS personalIdentificationNumber, "
          + "patient.birthDate AS birthDate "
          + "FROM Patient AS patient "
          + "JOIN patient.doctor AS doctor ON doctor.id = :doctorId"
  )
  List<PatientProjectionForCsvDownload> getDoctorsPatients(@Param("doctorId") UUID doctorId);
}
