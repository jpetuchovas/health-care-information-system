package com.justinaspetuchovas.health.controller;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import com.justinaspetuchovas.health.common.TimeProvider;
import com.justinaspetuchovas.health.model.record.MedicalRecord;
import com.justinaspetuchovas.health.repository.MedicalRecordRepository;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.text.MessageFormat;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.Mockito.when;
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
public class MedicalRecordControllerIT {
  private MockMvc mvc;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private MedicalRecordRepository medicalRecordRepository;

  @MockBean
  private TimeProvider timeProvider;

  @Before
  public void setup() {
    mvc = MockMvcBuilders
        .webAppContextSetup(webApplicationContext)
        .apply(springSecurity())
        .build();

    when(timeProvider.now())
        .thenReturn(TestDataConstants.CURRENT_TIME_FOR_TESTING);
  }

  private String getUriForCreateMedicalRecord(String patientId) {
    return MessageFormat.format("/api/patients/{0}/medical-records", patientId);
  }

  private String createRequestBodyForCreateMedicalRecord() {
    return new JSONObject()
        .put("description", "The condition is getting better.")
        .put("visitDurationInMinutes", 20)
        .put("diseaseCode", "A00.0")
        .put("isVisitCompensated", false)
        .put("isVisitRepeated", false)
        .toString();
  }

  private String getUriForGetMedicalRecords(String patientId) {
    return MessageFormat.format("/api/patients/{0}/medical-records/page", patientId);
  }

  @Test
  public void createMedicalRecordShouldDenyAccessForUnauthenticatedUser() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(createRequestBodyForCreateMedicalRecord())
        .when()
        .post(getUriForCreateMedicalRecord(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID))
        .then()
        .expect(status().isUnauthorized());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createMedicalRecordShouldDenyAccessForAdministrator() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(createRequestBodyForCreateMedicalRecord())
        .when()
        .post(getUriForCreateMedicalRecord(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID))
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME)
  public void createMedicalRecordShouldDenyAccessForPatient() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(createRequestBodyForCreateMedicalRecord())
        .when()
        .post(getUriForCreateMedicalRecord(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID))
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PHARMACIST_USERNAME)
  public void createMedicalRecordShouldDenyAccessForPharmacist() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(createRequestBodyForCreateMedicalRecord())
        .when()
        .post(getUriForCreateMedicalRecord(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID))
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITHOUT_PATIENTS_USERNAME)
  public void createMedicalRecordShouldAllowAccessForDoctorWithPatientNotAssignedToHim() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(createRequestBodyForCreateMedicalRecord())
        .when()
        .post(getUriForCreateMedicalRecord(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID))
        .then()
        .expect(status().isCreated());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void createMedicalRecordShouldAllowAccessForDoctorWithPatientAssignedToHim() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(createRequestBodyForCreateMedicalRecord())
        .when()
        .post(getUriForCreateMedicalRecord(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID))
        .then()
        .expect(status().isCreated());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void createMedicalRecordShouldDisallowCreatingMedicalRecordForNonExistentPatient() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(createRequestBodyForCreateMedicalRecord())
        .when()
        .post(getUriForCreateMedicalRecord(TestDataConstants.NON_EXISTENT_ID))
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void createMedicalRecordShouldCreateMedicalRecordWithCorrectData() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(createRequestBodyForCreateMedicalRecord())
        .post(getUriForCreateMedicalRecord(
            TestDataConstants.PATIENT_WITHOUT_MEDICAL_INFORMATION_ID
        ));

    Optional<MedicalRecord> newMedicalRecord = medicalRecordRepository
        .findAll()
        .stream()
        .filter(medicalRecord -> medicalRecord.getDiseaseCode().equals("A00.0"))
        .findAny();

    boolean isPresent = newMedicalRecord.isPresent();
    assertThat(isPresent, equalTo(true));

    if (isPresent) {
      MedicalRecord medicalRecord = newMedicalRecord.get();

      assertThat(
          medicalRecord.getDescription(),
          equalTo("The condition is getting better.")
      );
      assertThat(medicalRecord.getVisitDurationInMinutes(), equalTo((short) 20));
      assertThat(medicalRecord.getIsVisitCompensated(), equalTo(false));
      assertThat(medicalRecord.getIsVisitRepeated(), equalTo(false));
      assertThat(
          medicalRecord.getDate(),
          equalTo(TestDataConstants.CURRENT_TIME_FOR_TESTING)
      );
      assertThat(
          medicalRecord.getPatient().getId().toString(),
          equalTo(TestDataConstants.PATIENT_WITHOUT_MEDICAL_INFORMATION_ID)
      );
      assertThat(
          medicalRecord.getDoctor().getId().toString(),
          equalTo(TestDataConstants.DOCTOR_WITH_PATIENTS_ID)
      );
    }
  }

  @Test
  public void getMedicalRecordsShouldDenyAccessForUnauthenticatedUser() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 10))
        .when()
        .post(getUriForGetMedicalRecords(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID))
        .then()
        .expect(status().isUnauthorized());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void getMedicalRecordsShouldDenyAccessForAdministrator() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 10))
        .when()
        .post(getUriForGetMedicalRecords(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID))
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PHARMACIST_USERNAME)
  public void getMedicalRecordsShouldDenyAccessForPharmacist() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 10))
        .when()
        .post(getUriForGetMedicalRecords(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID))
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PATIENT_WITHOUT_MEDICAL_INFORMATION_USERNAME)
  public void getMedicalRecordsShouldDenyPatientToAccessAnotherPatientsMedicalRecords() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 10))
        .when()
        .post(getUriForGetMedicalRecords(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID))
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME)
  public void getMedicalRecordsShouldAllowPatientToAccessHisOwnMedicalRecords() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 10))
        .when()
        .post(getUriForGetMedicalRecords(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID))
        .then()
        .expect(status().isOk());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITHOUT_PATIENTS_USERNAME)
  public void getMedicalRecordsShouldDenyAccessForDoctorWithPatientNotAssignedToHim() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 10))
        .when()
        .post(getUriForGetMedicalRecords(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID))
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void getMedicalRecordsShouldAllowAccessForDoctorWithPatientAssignedToHim() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 10))
        .when()
        .post(getUriForGetMedicalRecords(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID))
        .then()
        .expect(status().isOk());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void getMedicalRecordsShouldSuccessfullyReturnMedicalRecords() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 10))
        .when()
        .post(getUriForGetMedicalRecords(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID))
        .then()
        .body("elements.size()", equalTo(2))
        .body(
            "elements.id",
            hasItems(
                TestDataConstants.FIRST_MEDICAL_RECORD_ID,
                TestDataConstants.SECOND_MEDICAL_RECORD_ID
            )
        ).body("hasNext", equalTo(false));
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void getMedicalRecordsShouldPerformPagination() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 1))
        .when()
        .post(getUriForGetMedicalRecords(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID))
        .then()
        .body("elements.size()", equalTo(1))
        .body("hasNext", equalTo(true));
  }
}
