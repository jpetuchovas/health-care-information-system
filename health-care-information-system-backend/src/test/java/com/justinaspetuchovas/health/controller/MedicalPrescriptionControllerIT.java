package com.justinaspetuchovas.health.controller;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import com.justinaspetuchovas.health.common.TimeProvider;
import com.justinaspetuchovas.health.model.prescription.MedicalPrescription;
import com.justinaspetuchovas.health.model.prescription.PurchaseFact;
import com.justinaspetuchovas.health.repository.MedicalPrescriptionRepository;
import com.justinaspetuchovas.health.repository.PurchaseFactRepository;
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

import java.math.BigDecimal;
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
public class MedicalPrescriptionControllerIT {
  private MockMvc mvc;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private MedicalPrescriptionRepository medicalPrescriptionRepository;

  @Autowired
  private PurchaseFactRepository purchaseFactRepository;

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

  private String getUriForCreateMedicalPrescription(String patientId) {
    return MessageFormat.format("/api/patients/{0}/medical-prescriptions", patientId);
  }

  private String createRequestBodyForCreateMedicalPrescription() {
    return new JSONObject()
        .put("activeIngredient", "Paracetamolis")
        .put("activeIngredientQuantity", 2.5)
        .put("activeIngredientMeasurementUnit", "MG")
        .put("usageDescription", "Vartoti du kartus per dieną.")
        .put("hasUnlimitedValidity", true)
        .toString();
  }

  private String getUriForGetMedicalPrescriptions(String patientId) {
    return MessageFormat.format("/api/patients/{0}/medical-prescriptions/page", patientId);
  }

  private String getUriForGetValidMedicalPrescriptions(String patientId) {
    return MessageFormat.format("/api/patients/{0}/medical-prescriptions/valid", patientId);
  }

  private String getUriForMarkMedicalPrescriptionsAsUsed(String patientId) {
    return MessageFormat.format("/api/patients/{0}/medical-prescriptions", patientId);
  }

  private String createRequestBodyForMarkMedicalPrescriptionsAsUsed(
      String firstMedicalPrescriptionId,
      String secondMedicalPrescriptionId
  ) {
    return MessageFormat.format(
        "[\"{0}\", \"{1}\"]",
        firstMedicalPrescriptionId,
        secondMedicalPrescriptionId
    );
  }

  @Test
  public void createMedicalPrescriptionShouldDenyAccessForUnauthenticatedUser() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(createRequestBodyForCreateMedicalPrescription())
        .when()
        .post(
            getUriForCreateMedicalPrescription(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .expect(status().isUnauthorized());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createMedicalPrescriptionShouldDenyAccessForAdministrator() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(createRequestBodyForCreateMedicalPrescription())
        .when()
        .post(
            getUriForCreateMedicalPrescription(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME)
  public void createMedicalPrescriptionShouldDenyAccessForPatient() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(createRequestBodyForCreateMedicalPrescription())
        .when()
        .post(
            getUriForCreateMedicalPrescription(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PHARMACIST_USERNAME)
  public void createMedicalPrescriptionShouldDenyAccessForPharmacist() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(createRequestBodyForCreateMedicalPrescription())
        .when()
        .post(
            getUriForCreateMedicalPrescription(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITHOUT_PATIENTS_USERNAME)
  public void createMedicalPrescriptionShouldAllowAccessForDoctorWithPatientNotAssignedToHim() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(createRequestBodyForCreateMedicalPrescription())
        .when()
        .post(
            getUriForCreateMedicalPrescription(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .expect(status().isCreated());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void createMedicalPrescriptionShouldAllowAccessForDoctorWithPatientAssignedToHim() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(createRequestBodyForCreateMedicalPrescription())
        .when()
        .post(
            getUriForCreateMedicalPrescription(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .expect(status().isCreated());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void
      createMedicalPrescriptionShouldDisallowCreatingMedicalPrescriptionForNonExistentPatient() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(createRequestBodyForCreateMedicalPrescription())
        .when()
        .post(
            getUriForCreateMedicalPrescription(
                TestDataConstants.NON_EXISTENT_ID
            )
        ).then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void
      createMedicalPrescriptionShouldCreateMedicalPrescriptionWithCorrectData() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(createRequestBodyForCreateMedicalPrescription())
        .post(getUriForCreateMedicalPrescription(
            TestDataConstants.PATIENT_WITHOUT_MEDICAL_INFORMATION_ID
        ));

    Optional<MedicalPrescription> newMedicalPrescription = medicalPrescriptionRepository
        .findAll()
        .stream()
        .filter(medicalPrescription ->
            medicalPrescription.getActiveIngredient().equals("Paracetamolis"))
        .findAny();

    boolean isPresent = newMedicalPrescription.isPresent();
    assertThat(isPresent, equalTo(true));

    if (isPresent) {
      MedicalPrescription medicalPrescription = newMedicalPrescription.get();

      assertThat(
          medicalPrescription.getActiveIngredientQuantity(),
          equalTo(new BigDecimal("2.500"))
      );
      assertThat(
          medicalPrescription.getActiveIngredientMeasurementUnit().name(),
          equalTo("MG")
      );
      assertThat(
          medicalPrescription.getUsageDescription(),
          equalTo("Vartoti du kartus per dieną.")
      );
      assertThat(
          medicalPrescription.getHasUnlimitedValidity(),
          equalTo(true)
      );
      assertThat(
          medicalPrescription.getValidityEndDate(),
          equalTo(null)
      );
      assertThat(
          medicalPrescription.getIssueDate(),
          equalTo(TestDataConstants.CURRENT_TIME_FOR_TESTING)
      );
      assertThat(
          medicalPrescription.getPatient().getId().toString(),
          equalTo(TestDataConstants.PATIENT_WITHOUT_MEDICAL_INFORMATION_ID)
      );
      assertThat(
          medicalPrescription.getDoctor().getId().toString(),
          equalTo(TestDataConstants.DOCTOR_WITH_PATIENTS_ID)
      );
    }
  }

  @Test
  public void getMedicalPrescriptionsShouldDenyAccessForUnauthenticatedUser() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 10))
        .when()
        .post(
            getUriForGetMedicalPrescriptions(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .expect(status().isUnauthorized());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void getMedicalPrescriptionsShouldDenyAccessForAdministrator() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 10))
        .when()
        .post(
            getUriForGetMedicalPrescriptions(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PHARMACIST_USERNAME)
  public void getMedicalPrescriptionsShouldDenyAccessForPharmacist() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 10))
        .when()
        .post(
            getUriForGetMedicalPrescriptions(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PATIENT_WITHOUT_MEDICAL_INFORMATION_USERNAME)
  public void
      getMedicalPrescriptionsShouldDenyPatientToAccessAnotherPatientsMedicalPrescriptions() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 10))
        .when()
        .post(
            getUriForGetMedicalPrescriptions(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME)
  public void getMedicalPrescriptionsShouldAllowPatientToAccessHisOwnMedicalPrescriptions() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 10))
        .when()
        .post(
            getUriForGetMedicalPrescriptions(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .expect(status().isOk());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITHOUT_PATIENTS_USERNAME)
  public void getMedicalPrescriptionsShouldDenyAccessForDoctorWithPatientNotAssignedToHim() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 10))
        .when()
        .post(
            getUriForGetMedicalPrescriptions(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void getMedicalPrescriptionsShouldAllowAccessForDoctorWithPatientAssignedToHim() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 10))
        .when()
        .post(
            getUriForGetMedicalPrescriptions(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .expect(status().isOk());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void getMedicalPrescriptionsShouldSuccessfullyReturnMedicalPrescriptions() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 10))
        .when()
        .post(
            getUriForGetMedicalPrescriptions(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .body("elements.size()", equalTo(3))
        .body(
            "elements.id",
            hasItems(
                TestDataConstants.INVALID_MEDICAL_PRESCRIPTION_ID,
                TestDataConstants.FIRST_VALID_MEDICAL_PRESCRIPTION_ID,
                TestDataConstants.SECOND_VALID_MEDICAL_PRESCRIPTION_ID
            )
        ).body("hasNext", equalTo(false));
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void getMedicalPrescriptionsShouldPerformPagination() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 1))
        .when()
        .post(
            getUriForGetMedicalPrescriptions(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .body("elements.size()", equalTo(1))
        .body("hasNext", equalTo(true));
  }

  @Test
  public void getValidMedicalPrescriptionsShouldDenyAccessForUnauthenticatedUser() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 10))
        .when()
        .post(
            getUriForGetValidMedicalPrescriptions(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .expect(status().isUnauthorized());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void getValidMedicalPrescriptionsShouldDenyAccessForAdministrator() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 10))
        .when()
        .post(
            getUriForGetValidMedicalPrescriptions(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void getValidMedicalPrescriptionsShouldDenyAccessForDoctor() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 10))
        .when()
        .post(
            getUriForGetValidMedicalPrescriptions(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME)
  public void getValidMedicalPrescriptionsShouldDenyAccessForPatient() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 10))
        .when()
        .post(
            getUriForGetValidMedicalPrescriptions(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PHARMACIST_USERNAME)
  public void getValidMedicalPrescriptionsShouldAllowAccessForPharmacist() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 10))
        .when()
        .post(
            getUriForGetValidMedicalPrescriptions(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .expect(status().isOk());
  }

  @Test
  @WithUserDetails(TestDataConstants.PHARMACIST_USERNAME)
  public void getValidMedicalPrescriptionsShouldReturnOnlyValidMedicalPrescriptions() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 10))
        .when()
        .post(
            getUriForGetValidMedicalPrescriptions(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .body("elements.size()", equalTo(2))
        .body(
            "elements.id",
            hasItems(
                TestDataConstants.FIRST_VALID_MEDICAL_PRESCRIPTION_ID,
                TestDataConstants.SECOND_VALID_MEDICAL_PRESCRIPTION_ID
            )
        ).body("hasNext", equalTo(false));
  }

  @Test
  @WithUserDetails(TestDataConstants.PHARMACIST_USERNAME)
  public void getValidMedicalPrescriptionsShouldPerformPagination() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(RequestUtil.createPageRequestBody(1, 1))
        .when()
        .post(
            getUriForGetValidMedicalPrescriptions(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .body("elements.size()", equalTo(1))
        .body("hasNext", equalTo(true));
  }

  @Test
  public void markMedicalPrescriptionsAsUsedShouldDenyAccessForUnauthenticatedUser() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(
            createRequestBodyForMarkMedicalPrescriptionsAsUsed(
                TestDataConstants.FIRST_VALID_MEDICAL_PRESCRIPTION_ID,
                TestDataConstants.SECOND_VALID_MEDICAL_PRESCRIPTION_ID
            )
        ).when()
        .put(
            getUriForMarkMedicalPrescriptionsAsUsed(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .expect(status().isUnauthorized());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void markMedicalPrescriptionsAsUsedShouldDenyAccessForAdministrator() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(
            createRequestBodyForMarkMedicalPrescriptionsAsUsed(
                TestDataConstants.FIRST_VALID_MEDICAL_PRESCRIPTION_ID,
                TestDataConstants.SECOND_VALID_MEDICAL_PRESCRIPTION_ID
            )
        ).when()
        .put(
            getUriForMarkMedicalPrescriptionsAsUsed(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME)
  public void markMedicalPrescriptionsAsUsedShouldDenyAccessForPatient() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(
            createRequestBodyForMarkMedicalPrescriptionsAsUsed(
                TestDataConstants.FIRST_VALID_MEDICAL_PRESCRIPTION_ID,
                TestDataConstants.SECOND_VALID_MEDICAL_PRESCRIPTION_ID
            )
        ).when()
        .put(
            getUriForMarkMedicalPrescriptionsAsUsed(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void markMedicalPrescriptionsAsUsedShouldDenyAccessForDoctor() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(
            createRequestBodyForMarkMedicalPrescriptionsAsUsed(
                TestDataConstants.FIRST_VALID_MEDICAL_PRESCRIPTION_ID,
                TestDataConstants.SECOND_VALID_MEDICAL_PRESCRIPTION_ID
            )
        ).when()
        .put(
            getUriForMarkMedicalPrescriptionsAsUsed(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PHARMACIST_USERNAME)
  public void markMedicalPrescriptionsAsUsedShouldAllowAccessForPharmacist() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(
            createRequestBodyForMarkMedicalPrescriptionsAsUsed(
                TestDataConstants.FIRST_VALID_MEDICAL_PRESCRIPTION_ID,
                TestDataConstants.SECOND_VALID_MEDICAL_PRESCRIPTION_ID
            )
        ).when()
        .put(
            getUriForMarkMedicalPrescriptionsAsUsed(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .expect(status().isNoContent());
  }

  @Test
  @WithUserDetails(TestDataConstants.PHARMACIST_USERNAME)
  public void
      markMedicalPrescriptionsAsUsedShouldDisallowMarkingNonExistentMedicalPrescriptionAsUsed() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(
            createRequestBodyForMarkMedicalPrescriptionsAsUsed(
                TestDataConstants.NON_EXISTENT_ID,
                TestDataConstants.SECOND_VALID_MEDICAL_PRESCRIPTION_ID
            )
        ).when()
        .put(
            getUriForMarkMedicalPrescriptionsAsUsed(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .expect(status().isBadRequest());
  }

  @SuppressWarnings("checkstyle:linelength")
  @Test
  @WithUserDetails(TestDataConstants.PHARMACIST_USERNAME)
  public void
      markMedicalPrescriptionsAsUsedShouldDisallowMarkingAnotherPatientsMedicalPrescriptionAsUsed() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(
            createRequestBodyForMarkMedicalPrescriptionsAsUsed(
                TestDataConstants.ANOTHER_PATIENTS_MEDICAL_PRESCRIPTION_ID,
                TestDataConstants.SECOND_VALID_MEDICAL_PRESCRIPTION_ID
            )
        ).when()
        .put(
            getUriForMarkMedicalPrescriptionsAsUsed(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.PHARMACIST_USERNAME)
  public void
      markMedicalPrescriptionsAsUsedShouldDisallowMarkingInvalidMedicalPrescriptionAsUsed() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(
            createRequestBodyForMarkMedicalPrescriptionsAsUsed(
                TestDataConstants.INVALID_MEDICAL_PRESCRIPTION_ID,
                TestDataConstants.SECOND_VALID_MEDICAL_PRESCRIPTION_ID
            )
        ).when()
        .put(
            getUriForMarkMedicalPrescriptionsAsUsed(
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.PHARMACIST_USERNAME)
  public void markMedicalPrescriptionsAsUsedShouldSuccessfullyMarkMedicalPrescriptionsAsUsed() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(
            createRequestBodyForMarkMedicalPrescriptionsAsUsed(
                TestDataConstants.FIRST_VALID_MEDICAL_PRESCRIPTION_ID,
                TestDataConstants.SECOND_VALID_MEDICAL_PRESCRIPTION_ID
            )
        ).put(getUriForMarkMedicalPrescriptionsAsUsed(
            TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
        ));

    Optional<PurchaseFact> firstMedicalPrescriptionPurchaseFact = purchaseFactRepository
        .findAll()
        .stream()
        .filter(purchaseFact ->
            purchaseFact.getPurchaseDate().equals(TestDataConstants.CURRENT_TIME_FOR_TESTING)
                &&  purchaseFact.getMedicalPrescription().getId().toString().equals(
                        TestDataConstants.FIRST_VALID_MEDICAL_PRESCRIPTION_ID
                    )
        ).findAny();

    boolean isFirstMedicalPrescriptionPurchaseFactPresent =
        firstMedicalPrescriptionPurchaseFact.isPresent();
    assertThat(isFirstMedicalPrescriptionPurchaseFactPresent, equalTo(true));

    if (isFirstMedicalPrescriptionPurchaseFactPresent) {
      PurchaseFact purchaseFact = firstMedicalPrescriptionPurchaseFact.get();

      assertThat(
          purchaseFact.getPharmacist().getId().toString(),
          equalTo(TestDataConstants.PHARMACIST_ID)
      );
    }

    Optional<PurchaseFact> secondMedicalPrescriptionPurchaseFact = purchaseFactRepository
        .findAll()
        .stream()
        .filter(purchaseFact ->
            purchaseFact.getPurchaseDate().equals(TestDataConstants.CURRENT_TIME_FOR_TESTING)
                &&  purchaseFact.getMedicalPrescription().getId().toString().equals(
                        TestDataConstants.SECOND_VALID_MEDICAL_PRESCRIPTION_ID
                    )
        ).findAny();

    boolean isSecondMedicalPrescriptionPurchaseFactPresent =
        secondMedicalPrescriptionPurchaseFact.isPresent();
    assertThat(isSecondMedicalPrescriptionPurchaseFactPresent, equalTo(true));

    if (isSecondMedicalPrescriptionPurchaseFactPresent) {
      PurchaseFact purchaseFact = secondMedicalPrescriptionPurchaseFact.get();

      assertThat(
          purchaseFact.getPharmacist().getId().toString(),
          equalTo(TestDataConstants.PHARMACIST_ID)
      );
    }
  }
}
