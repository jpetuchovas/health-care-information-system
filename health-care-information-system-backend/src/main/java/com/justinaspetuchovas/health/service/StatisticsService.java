package com.justinaspetuchovas.health.service;

import com.justinaspetuchovas.health.filter.VisitAggregateStatisticsFilter;
import com.justinaspetuchovas.health.filter.VisitDailyStatisticsFilter;
import com.justinaspetuchovas.health.pagination.SliceDto;
import com.justinaspetuchovas.health.repository.MedicalPrescriptionRepository;
import com.justinaspetuchovas.health.repository.MedicalRecordRepository;
import com.justinaspetuchovas.health.statistics.ActiveIngredientUsageCountProjection;
import com.justinaspetuchovas.health.statistics.DiseasePercentageProjection;
import com.justinaspetuchovas.health.statistics.VisitAggregateStatisticsProjection;
import com.justinaspetuchovas.health.statistics.VisitDailyStatisticsProjection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Service used to retrieve various disease, visit and active ingredients' statistics.
 */
@Service
public class StatisticsService {
  private static final Logger logger = LogManager.getLogger(StatisticsService.class);
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
  private final MedicalRecordRepository medicalRecordRepository;
  private final MedicalPrescriptionRepository medicalPrescriptionRepository;

  @Autowired
  public StatisticsService(
      MedicalRecordRepository medicalRecordRepository,
      MedicalPrescriptionRepository medicalPrescriptionRepository
  ) {
    this.medicalRecordRepository = medicalRecordRepository;
    this.medicalPrescriptionRepository = medicalPrescriptionRepository;
  }

  @Transactional(readOnly = true)
  public List<DiseasePercentageProjection> getMostFrequentDiseasePercentages(
      short numberOfDiseases
  ) {
    logger.info(
        "Returned {} most frequent diseases with their frequency percentages.",
        numberOfDiseases
    );

    return medicalRecordRepository.getMostFrequentDiseasePercentages(
        new PageRequest(0, numberOfDiseases)
    );
  }

  @Transactional(readOnly = true)
  public List<ActiveIngredientUsageCountProjection> getMostOftenUsedActiveIngredientCounts(
      short numberOfActiveIngredients
  ) {
    logger.info(
        "Returned {} most often used active ingredients with their usage counts.",
        numberOfActiveIngredients
    );

    return medicalPrescriptionRepository.getMostOftenUsedActiveIngredientCounts(
        new PageRequest(0, numberOfActiveIngredients)
    );
  }

  @Transactional(readOnly = true)
  public VisitAggregateStatisticsProjection getVisitAggregateStatistics(
      UUID doctorId,
      VisitAggregateStatisticsFilter visitAggregateStatisticsFilter
  ) {
    Date startDate = visitAggregateStatisticsFilter.getStartDate();
    Date endDate = visitAggregateStatisticsFilter.getEndDate();

    logger.info(
        "Returned the doctor's aggregate visit statistics for the period from {} to {}.",
        dateFormat.format(startDate),
        dateFormat.format(endDate)
    );

    return medicalRecordRepository.getVisitAggregateStatistics(doctorId, startDate, endDate);
  }

  @Transactional(readOnly = true)
  public SliceDto getVisitDailyStatistics(
      UUID doctorId,
      VisitDailyStatisticsFilter visitDailyStatisticsFilter
  ) {
    int pageNumber = visitDailyStatisticsFilter.getPageNumber();
    int pageSize = visitDailyStatisticsFilter.getPageSize();
    Date startDate = visitDailyStatisticsFilter.getStartDate();
    Date endDate = visitDailyStatisticsFilter.getEndDate();

    Slice<VisitDailyStatisticsProjection> visitDailyStatisticsSlice =
        medicalRecordRepository.getVisitDailyStatistics(
            doctorId,
            startDate,
            endDate,
            new PageRequest(pageNumber, pageSize, Sort.Direction.ASC, "date")
        );

    logger.info(
        "Returned a page number {} of size {} with the doctor's daily visits statistics "
            + "for the period from {} to {}.",
        pageNumber,
        pageSize,
        dateFormat.format(startDate),
        dateFormat.format(endDate)
    );

    return new SliceDto(
        visitDailyStatisticsSlice.getContent(),
        visitDailyStatisticsSlice.hasNext()
    );
  }
}
