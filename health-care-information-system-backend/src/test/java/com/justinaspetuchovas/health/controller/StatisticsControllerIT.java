package com.justinaspetuchovas.health.controller;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.text.MessageFormat;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SqlGroup({
    @Sql(
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        scripts = "classpath:test_data.sql"
    ),
    @Sql(
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
        scripts = "classpath:delete_test_data.sql"
    )
})
public class StatisticsControllerIT {
  private static final String URI_FOR_GET_MOST_FREQUENT_DISEASE_PERCENTAGES =
      "/api/public/statistics/diseases";
  private static final String URI_FOR_GET_MOST_OFTEN_USED_ACTIVE_INGREDIENT_COUNTS =
      "/api/public/statistics/active-ingredients";

  private MockMvc mvc;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Before
  public void setup() {
    mvc = MockMvcBuilders
        .webAppContextSetup(webApplicationContext)
        .apply(springSecurity())
        .build();
  }

  private String getUriForGetVisitDailyStatistics(String doctorId) {
    return MessageFormat.format("/api/doctors/{0}/statistics/visits/daily", doctorId);
  }

  private String createRequestBodyForGetVisitDailyStatistics(
      int pageNumber,
      int pageSize,
      String startDate,
      String endDate
  ) {
    return new JSONObject()
        .put("pageNumber", pageNumber)
        .put("pageSize", pageSize)
        .put("startDate", startDate)
        .put("endDate", endDate)
        .toString();
  }

  private String getUriForGetVisitAggregateStatistics(String doctorId) {
    return MessageFormat.format("/api/doctors/{0}/statistics/visits/aggregate", doctorId);
  }

  private String createRequestBodyForGetVisitAggregateStatistics(String startDate, String endDate) {
    return new JSONObject()
        .put("startDate", startDate)
        .put("endDate", endDate)
        .toString();
  }

  @Test
  public void getMostFrequentDiseasePercentagesShouldAllowAccessForAllUsers() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .when()
        .get(URI_FOR_GET_MOST_FREQUENT_DISEASE_PERCENTAGES)
        .then()
        .expect(status().isOk());
  }

  @Test
  public void
      getMostFrequentDiseasePercentagesShouldReturnDiseasesOrderedByPercentagesDescending() {
    MockMvcResponse response = RestAssuredMockMvc.given().mockMvc(mvc)
        .when()
        .get(URI_FOR_GET_MOST_FREQUENT_DISEASE_PERCENTAGES)
        .then()
        .body("size()", equalTo(2))
        .body("diseaseCode", hasItems("A54.42", "A54.41"))
        .extract()
        .response();

    double mostFrequentDiseasePercentage = (float) response.path("[0].percentage");
    double leastFrequentDiseasePercentage = (float) response.path("[1].percentage");

    assertThat(mostFrequentDiseasePercentage, closeTo(66.67, 0.004));
    assertThat(leastFrequentDiseasePercentage, closeTo(33.33, 0.004));
  }

  @Test
  public void getMostOftenUsedActiveIngredientCountsShouldAllowAccessForAllUsers() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .when()
        .get(URI_FOR_GET_MOST_OFTEN_USED_ACTIVE_INGREDIENT_COUNTS)
        .then()
        .expect(status().isOk());
  }

  @SuppressWarnings("checkstyle:linelength")
  @Test
  public void
      getMostOftenUsedActiveIngredientCountsShouldReturnActiveIngredientOrderedByCountsDescending() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .when()
        .get(URI_FOR_GET_MOST_OFTEN_USED_ACTIVE_INGREDIENT_COUNTS)
        .then()
        .body("size()", equalTo(2))
        .body("activeIngredient", hasItems("Alfuzozinas", "Betametazonas"))
        .body("[0].usageCount", equalTo(3))
        .body("[1].usageCount", equalTo(1));
  }

  @Test
  public void getVisitAggregateStatisticsShouldDenyAccessForUnauthenticatedUser() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(
            createRequestBodyForGetVisitAggregateStatistics(
                TestDataConstants.EARLIEST_MEDICAL_RECORD_DATE,
                TestDataConstants.LATEST_MEDICAL_RECORD_DATE
            )
        ).when()
        .post(getUriForGetVisitAggregateStatistics(TestDataConstants.DOCTOR_WITH_PATIENTS_ID))
        .then()
        .expect(status().isUnauthorized());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void getVisitAggregateStatisticsShouldDenyAccessForAdministrator() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(
            createRequestBodyForGetVisitAggregateStatistics(
                TestDataConstants.EARLIEST_MEDICAL_RECORD_DATE,
                TestDataConstants.LATEST_MEDICAL_RECORD_DATE
            )
        ).when()
        .post(getUriForGetVisitAggregateStatistics(TestDataConstants.DOCTOR_WITH_PATIENTS_ID))
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME)
  public void getVisitAggregateStatisticsShouldDenyAccessForPatient() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(
            createRequestBodyForGetVisitAggregateStatistics(
                TestDataConstants.EARLIEST_MEDICAL_RECORD_DATE,
                TestDataConstants.LATEST_MEDICAL_RECORD_DATE
            )
        ).when()
        .post(getUriForGetVisitAggregateStatistics(TestDataConstants.DOCTOR_WITH_PATIENTS_ID))
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PHARMACIST_USERNAME)
  public void getVisitAggregateStatisticsShouldDenyAccessForPharmacist() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(
            createRequestBodyForGetVisitAggregateStatistics(
                TestDataConstants.EARLIEST_MEDICAL_RECORD_DATE,
                TestDataConstants.LATEST_MEDICAL_RECORD_DATE
            )
        ).when()
        .post(getUriForGetVisitAggregateStatistics(TestDataConstants.DOCTOR_WITH_PATIENTS_ID))
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITHOUT_PATIENTS_USERNAME)
  public void
      getVisitAggregateStatisticsShouldDenyDoctorToAccessAnotherDoctorsAggregateVisitStatistics() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(
            createRequestBodyForGetVisitAggregateStatistics(
                TestDataConstants.EARLIEST_MEDICAL_RECORD_DATE,
                TestDataConstants.LATEST_MEDICAL_RECORD_DATE
            )
        ).when()
        .post(getUriForGetVisitAggregateStatistics(TestDataConstants.DOCTOR_WITH_PATIENTS_ID))
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void getVisitAggregateStatisticsShouldAllowDoctorToAccessHisOwnAggregateVisitStatistics() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(
            createRequestBodyForGetVisitAggregateStatistics(
                TestDataConstants.EARLIEST_MEDICAL_RECORD_DATE,
                TestDataConstants.LATEST_MEDICAL_RECORD_DATE
            )
        ).when()
        .post(getUriForGetVisitAggregateStatistics(TestDataConstants.DOCTOR_WITH_PATIENTS_ID))
        .then()
        .expect(status().isOk());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void getVisitAggregateStatisticsShouldSuccessfullyReturnAggregateVisitStatistics() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(
            createRequestBodyForGetVisitAggregateStatistics(
                TestDataConstants.EARLIEST_MEDICAL_RECORD_DATE,
                TestDataConstants.LATEST_MEDICAL_RECORD_DATE
            )
        ).when()
        .post(getUriForGetVisitAggregateStatistics(TestDataConstants.DOCTOR_WITH_PATIENTS_ID))
        .then()
        .body("visitAggregateCount", equalTo(4))
        .body("visitDurationInMinutesAggregateSum", equalTo(286));
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void getVisitAggregateStatisticsShouldSetStatisticsToZeroWhenNoVisitsHappened() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(
            createRequestBodyForGetVisitAggregateStatistics(
                TestDataConstants.DATE_WITH_NO_MEDICAL_RECORDS,
                TestDataConstants.DATE_WITH_NO_MEDICAL_RECORDS
            )
        ).when()
        .post(getUriForGetVisitAggregateStatistics(TestDataConstants.DOCTOR_WITH_PATIENTS_ID))
        .then()
        .body("visitAggregateCount", equalTo(0))
        .body("visitDurationInMinutesAggregateSum", equalTo(0));
  }

  @Test
  public void getVisitDailyStatisticsShouldDenyAccessForUnauthenticatedUser() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(
            createRequestBodyForGetVisitDailyStatistics(
                1,
                10,
                TestDataConstants.EARLIEST_MEDICAL_RECORD_DATE,
                TestDataConstants.LATEST_MEDICAL_RECORD_DATE
            )
        ).when()
        .post(getUriForGetVisitDailyStatistics(TestDataConstants.DOCTOR_WITH_PATIENTS_ID))
        .then()
        .expect(status().isUnauthorized());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void getVisitDailyStatisticsShouldDenyAccessForAdministrator() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(
            createRequestBodyForGetVisitDailyStatistics(
                1,
                10,
                TestDataConstants.EARLIEST_MEDICAL_RECORD_DATE,
                TestDataConstants.LATEST_MEDICAL_RECORD_DATE
            )
        ).when()
        .post(getUriForGetVisitDailyStatistics(TestDataConstants.DOCTOR_WITH_PATIENTS_ID))
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME)
  public void getVisitDailyStatisticsShouldDenyAccessForPatient() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(
            createRequestBodyForGetVisitDailyStatistics(
                1,
                10,
                TestDataConstants.EARLIEST_MEDICAL_RECORD_DATE,
                TestDataConstants.LATEST_MEDICAL_RECORD_DATE
            )
        ).when()
        .post(getUriForGetVisitDailyStatistics(TestDataConstants.DOCTOR_WITH_PATIENTS_ID))
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PHARMACIST_USERNAME)
  public void getVisitDailyStatisticsShouldDenyAccessForPharmacist() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(
            createRequestBodyForGetVisitDailyStatistics(
                1,
                10,
                TestDataConstants.EARLIEST_MEDICAL_RECORD_DATE,
                TestDataConstants.LATEST_MEDICAL_RECORD_DATE
            )
        ).when()
        .post(getUriForGetVisitDailyStatistics(TestDataConstants.DOCTOR_WITH_PATIENTS_ID))
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITHOUT_PATIENTS_USERNAME)
  public void getVisitDailyStatisticsShouldDenyDoctorToAccessAnotherDoctorsDailyVisitStatistics() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(
            createRequestBodyForGetVisitDailyStatistics(
                1,
                10,
                TestDataConstants.EARLIEST_MEDICAL_RECORD_DATE,
                TestDataConstants.LATEST_MEDICAL_RECORD_DATE
            )
        ).when()
        .post(getUriForGetVisitDailyStatistics(TestDataConstants.DOCTOR_WITH_PATIENTS_ID))
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void getVisitDailyStatisticsShouldAllowDoctorToAccessHisOwnDailyVisitStatistics() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(
            createRequestBodyForGetVisitDailyStatistics(
                1,
                10,
                TestDataConstants.EARLIEST_MEDICAL_RECORD_DATE,
                TestDataConstants.LATEST_MEDICAL_RECORD_DATE
            )
        ).when()
        .post(getUriForGetVisitDailyStatistics(TestDataConstants.DOCTOR_WITH_PATIENTS_ID))
        .then()
        .expect(status().isOk());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void getVisitDailyStatisticsShouldSuccessfullyReturnDailyVisitStatistics() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(
            createRequestBodyForGetVisitDailyStatistics(
                1,
                10,
                TestDataConstants.EARLIEST_MEDICAL_RECORD_DATE,
                TestDataConstants.LATEST_MEDICAL_RECORD_DATE
            )
        ).when()
        .post(getUriForGetVisitDailyStatistics(TestDataConstants.DOCTOR_WITH_PATIENTS_ID))
        .then()
        .body("elements.size()", equalTo(3))
        .body(
            "elements.date",
            hasItems(
                TestDataConstants.EARLIEST_MEDICAL_RECORD_DATE,
                TestDataConstants.LATEST_MEDICAL_RECORD_DATE
            )
        ).body(
            MessageFormat.format(
                "elements.find '{' it.date == ''{0}'' '}'.visitCount",
                TestDataConstants.EARLIEST_MEDICAL_RECORD_DATE
            ),
            equalTo(2)
        ).body(
            MessageFormat.format(
                "elements.find '{' it.date == ''{0}'' '}'.visitDurationInMinutesSum",
                TestDataConstants.EARLIEST_MEDICAL_RECORD_DATE
            ),
            equalTo(165)
        ).body(
            MessageFormat.format(
                "elements.find '{' it.date == ''{0}'' '}'.visitCount",
                TestDataConstants.LATEST_MEDICAL_RECORD_DATE
            ),
            equalTo(1)
        ).body(
            MessageFormat.format(
                "elements.find '{' it.date == ''{0}'' '}'.visitDurationInMinutesSum",
                TestDataConstants.LATEST_MEDICAL_RECORD_DATE
            ),
            equalTo(60)
        ).body("hasNext", equalTo(false));
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void getVisitDailyStatisticsShouldReturnEmptyListWhenNoVisitsHappened() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(
            createRequestBodyForGetVisitDailyStatistics(
                1,
                10,
                TestDataConstants.DATE_WITH_NO_MEDICAL_RECORDS,
                TestDataConstants.DATE_WITH_NO_MEDICAL_RECORDS
            )
        ).when()
        .post(getUriForGetVisitDailyStatistics(TestDataConstants.DOCTOR_WITH_PATIENTS_ID))
        .then()
        .body("elements.size()", equalTo(0))
        .body("hasNext", equalTo(false));
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void getVisitDailyStatisticsShouldPerformPagination() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(
            createRequestBodyForGetVisitDailyStatistics(
                1,
                1,
                TestDataConstants.EARLIEST_MEDICAL_RECORD_DATE,
                TestDataConstants.LATEST_MEDICAL_RECORD_DATE
            )
        ).when()
        .post(getUriForGetVisitDailyStatistics(TestDataConstants.DOCTOR_WITH_PATIENTS_ID))
        .then()
        .expect(status().isOk())
        .body("elements.size()", equalTo(1))
        .body("hasNext", equalTo(true));
  }
}
