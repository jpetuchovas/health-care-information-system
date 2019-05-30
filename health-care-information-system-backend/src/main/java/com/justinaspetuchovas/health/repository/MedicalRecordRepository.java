package com.justinaspetuchovas.health.repository;

import com.justinaspetuchovas.health.model.record.MedicalRecord;
import com.justinaspetuchovas.health.model.record.MedicalRecordProjection;
import com.justinaspetuchovas.health.statistics.DiseasePercentageProjection;
import com.justinaspetuchovas.health.statistics.VisitAggregateStatisticsProjection;
import com.justinaspetuchovas.health.statistics.VisitDailyStatisticsProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Repository for {@link MedicalRecord} entities.
 */
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, UUID> {
  @Query(
      "SELECT medicalRecord.id AS id, "
          + "medicalRecord.description AS description, "
          + "medicalRecord.visitDurationInMinutes AS visitDurationInMinutes, "
          + "medicalRecord.diseaseCode AS diseaseCode, "
          + "medicalRecord.isVisitCompensated AS isVisitCompensated, "
          + "medicalRecord.isVisitRepeated AS isVisitRepeated, "
          + "medicalRecord.date AS date, "
          + "CONCAT(doctor.firstName, ' ', doctor.lastName) AS doctorFullName "
          + "FROM MedicalRecord AS medicalRecord "
          + "JOIN medicalRecord.patient AS patient ON patient.id = :patientId "
          + "JOIN medicalRecord.doctor AS doctor"
  )
  Slice<MedicalRecordProjection> getMedicalRecords(
      @Param("patientId") UUID patientId,
      Pageable pageable
  );

  long countByIsVisitRepeated(boolean isVisitRepeated);

  @Query(
      "SELECT medicalRecord.diseaseCode AS diseaseCode, "
          + "(COUNT(medicalRecord) * 1.0) / :nonRepeatedVisitCount * 100 AS percentage "
          + "FROM MedicalRecord AS medicalRecord "
          + "WHERE medicalRecord.isVisitRepeated = FALSE "
          + "GROUP BY medicalRecord.diseaseCode "
          + "ORDER BY (COUNT(medicalRecord) * 1.0) / :nonRepeatedVisitCount * 100 DESC"
  )
  List<DiseasePercentageProjection> getMostFrequentDiseasePercentagesOfNonRepeatedVisits(
      // double data type is used here to prevent integer division.
      @Param("nonRepeatedVisitCount") double nonRepeatedVisitCount,
      Pageable pageable
  );

  default List<DiseasePercentageProjection> getMostFrequentDiseasePercentages(Pageable pageable) {
    return getMostFrequentDiseasePercentagesOfNonRepeatedVisits(
        countByIsVisitRepeated(false),
        pageable
    );
  }

  @Query(
      "SELECT COUNT(medicalRecord.id) AS visitAggregateCount, "
          + "COALESCE(SUM(medicalRecord.visitDurationInMinutes), 0) "
          + "AS visitDurationInMinutesAggregateSum "
          + "FROM MedicalRecord AS medicalRecord "
          + "JOIN medicalRecord.doctor AS doctor ON doctor.id = :doctorId "
          + "WHERE medicalRecord.date BETWEEN :startDate AND :endDate"
  )
  VisitAggregateStatisticsProjection getVisitAggregateStatistics(
      @Param("doctorId") UUID doctorId,
      @Param("startDate") Date startDate,
      @Param("endDate") Date endDate
  );

  @Query(
      "SELECT medicalRecord.date AS date, "
          + "COUNT(medicalRecord.id) AS visitCount, "
          + "COALESCE(SUM(medicalRecord.visitDurationInMinutes), 0) AS visitDurationInMinutesSum "
          + "FROM MedicalRecord AS medicalRecord "
          + "JOIN medicalRecord.doctor AS doctor ON doctor.id = :doctorId "
          + "WHERE medicalRecord.date BETWEEN :startDate AND :endDate "
          + "GROUP BY medicalRecord.date "
          + "HAVING COUNT(medicalRecord.id) > 0"
  )
  Slice<VisitDailyStatisticsProjection> getVisitDailyStatistics(
      @Param("doctorId") UUID doctorId,
      @Param("startDate") Date startDate,
      @Param("endDate") Date endDate,
      Pageable pageable
  );
}
