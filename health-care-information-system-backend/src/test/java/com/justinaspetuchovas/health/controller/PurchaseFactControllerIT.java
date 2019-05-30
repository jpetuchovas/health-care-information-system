package com.justinaspetuchovas.health.controller;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
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
public class PurchaseFactControllerIT {
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

  private String getUriForGetPurchaseFacts(String patientId, String medicalPrescriptionId) {
    return MessageFormat.format(
        "/api/patients/{0}/medical-prescriptions/{1}/purchase-facts",
        patientId,
        medicalPrescriptionId
    );
  }

  @Test
  public void getPurchaseFactsShouldDenyAccessForUnauthenticatedUser() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 10))
        .when()
        .post(
            getUriForGetPurchaseFacts(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID,
                TestDataConstants.FIRST_VALID_MEDICAL_PRESCRIPTION_ID
            )
        ).then()
        .expect(status().isUnauthorized());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void getPurchaseFactsShouldDenyAccessForAdministrator() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 10))
        .when()
        .post(
            getUriForGetPurchaseFacts(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID,
                TestDataConstants.FIRST_VALID_MEDICAL_PRESCRIPTION_ID
            )
        ).then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PHARMACIST_USERNAME)
  public void getPurchaseFactsShouldDenyAccessForPharmacist() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 10))
        .when()
        .post(
            getUriForGetPurchaseFacts(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID,
                TestDataConstants.FIRST_VALID_MEDICAL_PRESCRIPTION_ID
            )
        ).then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PATIENT_WITHOUT_MEDICAL_INFORMATION_USERNAME)
  public void getPurchaseFactsShouldDenyPatientToAccessAnotherPatientsPurchaseFacts() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 10))
        .when()
        .post(
            getUriForGetPurchaseFacts(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID,
                TestDataConstants.FIRST_VALID_MEDICAL_PRESCRIPTION_ID
            )
        ).then()
        .expect(status().isForbidden());
  }

  @SuppressWarnings("checkstyle:linelength")
  @Test
  @WithUserDetails(TestDataConstants.PATIENT_WITHOUT_MEDICAL_INFORMATION_USERNAME)
  public void
      getPurchaseFactsShouldDenyPatientToAccessPurchaseFactsOfMedicalPrescriptionNotBelongingToHim() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 10))
        .when()
        .post(
            getUriForGetPurchaseFacts(
                TestDataConstants.PATIENT_WITHOUT_MEDICAL_INFORMATION_ID,
                TestDataConstants.FIRST_VALID_MEDICAL_PRESCRIPTION_ID
            )
        ).then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME)
  public void getPurchaseFactsShouldAllowPatientToAccessHisOwnPurchaseFacts() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 10))
        .when()
        .post(
            getUriForGetPurchaseFacts(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID,
                TestDataConstants.FIRST_VALID_MEDICAL_PRESCRIPTION_ID
            )
        ).then()
        .expect(status().isOk());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITHOUT_PATIENTS_USERNAME)
  public void getPurchaseFactsShouldDenyAccessForDoctorWithPatientNotAssignedToHim() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 10))
        .when()
        .post(
            getUriForGetPurchaseFacts(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID,
                TestDataConstants.FIRST_VALID_MEDICAL_PRESCRIPTION_ID
            )
        ).then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void getPurchaseFactsShouldAllowAccessForDoctorWithPatientAssignedToHim() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 10))
        .when()
        .post(
            getUriForGetPurchaseFacts(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID,
                TestDataConstants.FIRST_VALID_MEDICAL_PRESCRIPTION_ID
            )
        ).then()
        .expect(status().isOk());
  }

  @Test
  @WithUserDetails(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME)
  public void getPurchaseFactsShouldSuccessfullyReturnPurchaseFacts() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 10))
        .when()
        .post(
            getUriForGetPurchaseFacts(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID,
                TestDataConstants.FIRST_VALID_MEDICAL_PRESCRIPTION_ID
            )
        ).then()
        .body("elements.size()", equalTo(2))
        .body(
            "elements",
            hasItems(
                TestDataConstants.FIRST_PURCHASE_FACT_PURCHASE_DATE,
                TestDataConstants.SECOND_PURCHASE_FACT_PURCHASE_DATE
            )
        ).body("hasNext", equalTo(false));
  }

  @Test
  @WithUserDetails(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME)
  public void getPurchaseFactsShouldPerformPagination() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 1))
        .when()
        .post(
            getUriForGetPurchaseFacts(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID,
                TestDataConstants.FIRST_VALID_MEDICAL_PRESCRIPTION_ID
            )
        ).then()
        .body("elements.size()", equalTo(1))
        .body("hasNext", equalTo(true));
  }
}
