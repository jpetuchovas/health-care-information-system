package com.justinaspetuchovas.health.controller;

import com.justinaspetuchovas.health.exception.ForbiddenException;
import com.justinaspetuchovas.health.filter.VisitAggregateStatisticsFilter;
import com.justinaspetuchovas.health.filter.VisitDailyStatisticsFilter;
import com.justinaspetuchovas.health.pagination.SliceDto;
import com.justinaspetuchovas.health.security.JwtUser;
import com.justinaspetuchovas.health.service.StatisticsService;
import com.justinaspetuchovas.health.statistics.ActiveIngredientUsageCountProjection;
import com.justinaspetuchovas.health.statistics.DiseasePercentageProjection;
import com.justinaspetuchovas.health.statistics.VisitAggregateStatisticsProjection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * Controller that handles requests to retrieve various statistics.
 */
@RestController
@RequestMapping("/api")
public class StatisticsController {
  private static final Logger logger = LogManager.getLogger(StatisticsController.class);
  private static final short NUMBER_OF_DISEASES = 10;
  private static final short NUMBER_OF_ACTIVE_INGREDIENTS = 10;
  private final StatisticsService statisticsService;

  @Autowired
  public StatisticsController(StatisticsService statisticsService) {
    this.statisticsService = statisticsService;
  }

  @GetMapping("/public/statistics/diseases")
  public List<DiseasePercentageProjection> getMostFrequentDiseasePercentages() {
    return statisticsService.getMostFrequentDiseasePercentages(NUMBER_OF_DISEASES);
  }

  @GetMapping("/public/statistics/active-ingredients")
  public List<ActiveIngredientUsageCountProjection> getMostOftenUsedActiveIngredientCounts() {
    return statisticsService.getMostOftenUsedActiveIngredientCounts(
        NUMBER_OF_ACTIVE_INGREDIENTS
    );
  }

  @PostMapping("/doctors/{doctorId}/statistics/visits/aggregate")
  public VisitAggregateStatisticsProjection getVisitAggregateStatistics(
      @PathVariable UUID doctorId,
      @RequestBody VisitAggregateStatisticsFilter visitAggregateStatisticsFilter,
      Authentication authentication
  ) {
    UUID userId = ((JwtUser) authentication.getPrincipal()).getId();

    if (!userId.equals(doctorId)) {
      logger.warn(
          "Denied access because the doctor with id \"{}\" tried to access another doctor's "
              + "with id \"{}\" aggregate visit statistics.",
          userId,
          doctorId
      );
      throw new ForbiddenException();
    } else {
      return statisticsService.getVisitAggregateStatistics(
          doctorId,
          visitAggregateStatisticsFilter
      );
    }
  }

  @PostMapping("/doctors/{doctorId}/statistics/visits/daily")
  public SliceDto getVisitDailyStatistics(
      @PathVariable UUID doctorId,
      @Valid @RequestBody VisitDailyStatisticsFilter visitDailyStatisticsFilter,
      Authentication authentication
  ) {
    UUID userId = ((JwtUser) authentication.getPrincipal()).getId();

    if (!userId.equals(doctorId)) {
      logger.warn(
          "Denied access because the doctor with id \"{}\" tried to access another doctor's "
              + "with id \"{}\" daily visit statistics.",
          userId,
          doctorId
      );
      throw new ForbiddenException();
    } else {
      return statisticsService.getVisitDailyStatistics(doctorId, visitDailyStatisticsFilter);
    }
  }
}
