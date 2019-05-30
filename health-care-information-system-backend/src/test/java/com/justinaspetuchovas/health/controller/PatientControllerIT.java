package com.justinaspetuchovas.health.controller;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import com.justinaspetuchovas.health.common.TimeProvider;
import com.justinaspetuchovas.health.common.ValidationConstants;
import com.justinaspetuchovas.health.model.user.Role;
import com.justinaspetuchovas.health.model.user.patient.Patient;
import com.justinaspetuchovas.health.repository.PatientRepository;
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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
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
public class PatientControllerIT {
  private static final String URI_FOR_CREATE_PATIENT = "/api/patients";
  private static final String URI_FOR_GET_PATIENT_BY_PERSONAL_ID_NUMBER =
      "/api/patients/filter/personal-identification-number";
  private static final String URI_FOR_FILTER_PATIENTS_FOR_ADMINISTRATOR =
      "/api/patients/filter/admin";

  private MockMvc mvc;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private PatientRepository patientRepository;

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

  private String createRequestBodyForCreatePatient(
      String firstName,
      String lastName,
      String birthDate,
      String personalIdentificationNumber,
      String username,
      String password
  ) {
    return new JSONObject()
        .put("firstName", RequestUtil.replaceNullWithJsonNull(firstName))
        .put("lastName", RequestUtil.replaceNullWithJsonNull(lastName))
        .put("birthDate", RequestUtil.replaceNullWithJsonNull(birthDate))
        .put(
            "personalIdentificationNumber",
            RequestUtil.replaceNullWithJsonNull(personalIdentificationNumber)
        ).put("username", RequestUtil.replaceNullWithJsonNull(username))
        .put("password", RequestUtil.replaceNullWithJsonNull(password))
        .toString();
  }

  private String getUriForGetPatientById(String patientId) {
    return MessageFormat.format("/api/patients/{0}", patientId);
  }

  private String createRequestBodyForGetPatientByPersonalId(String personalIdentificationNumber) {
    return new JSONObject()
        .put("personalIdentificationNumber", personalIdentificationNumber)
        .toString();
  }

  private String createRequestBodyForFilterPatientsForAdministrator(
      String firstName,
      String lastName,
      String personalIdentificationNumber,
      String username,
      int pageNumber,
      int pageSize
  ) {
    return new JSONObject()
        .put("firstName", firstName)
        .put("lastName", lastName)
        .put("personalIdentificationNumber", personalIdentificationNumber)
        .put("username", username)
        .put("pageNumber", pageNumber)
        .put("pageSize", pageSize)
        .toString();
  }

  private String getUriForFilterPatientsForDoctor(String doctorId) {
    return MessageFormat.format("/api/doctors/{0}/patients", doctorId);
  }

  private String createRequestBodyForFilterPatientsForDoctor(
      String firstName,
      String lastName,
      String personalIdentificationNumber,
      String diseaseCode,
      int pageNumber,
      int pageSize
  ) {
    return new JSONObject()
        .put("firstName", firstName)
        .put("lastName", lastName)
        .put("personalIdentificationNumber", personalIdentificationNumber)
        .put("diseaseCode", diseaseCode)
        .put("pageNumber", pageNumber)
        .put("pageSize", pageSize)
        .toString();
  }

  private String getUriForDownloadPatientCsv(String doctorId) {
    return MessageFormat.format("/api/doctors/{0}/patients/csv", doctorId);
  }

  @Test
  public void createPatientShouldDenyAccessForUnauthenticatedUser() {
    String requestBody = createRequestBodyForCreatePatient(
        "Name",
        "Surname",
        "1989-01-16",
        "38901160195",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PATIENT)
        .then()
        .expect(status().isUnauthorized());
  }

  @Test
  @WithUserDetails(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME)
  public void createPatientShouldDenyAccessForPatient() {
    String requestBody = createRequestBodyForCreatePatient(
        "Name",
        "Surname",
        "1989-01-16",
        "38901160195",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PATIENT)
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void createPatientShouldDenyAccessForDoctor() {
    String requestBody = createRequestBodyForCreatePatient(
        "Name",
        "Surname",
        "1989-01-16",
        "38901160195",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PATIENT)
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PHARMACIST_USERNAME)
  public void createPatientShouldDenyAccessForPharmacist() {
    String requestBody = createRequestBodyForCreatePatient(
        "Name",
        "Surname",
        "1989-01-16",
        "38901160195",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PATIENT)
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPatientShouldAllowAccessForAdministrator() {
    String requestBody = createRequestBodyForCreatePatient(
        "Name",
        "Surname",
        "1989-01-16",
        "38901160195",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PATIENT)
        .then()
        .expect(status().isCreated());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPatientShouldDisallowCreatingPatientWithExistingUsername() {
    String username = "username";

    String requestBodyWithNonExistentUsername = createRequestBodyForCreatePatient(
        "Name",
        "Surname",
        "1989-01-16",
        "38901160195",
        username,
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBodyWithNonExistentUsername)
        .when()
        .post(URI_FOR_CREATE_PATIENT);

    String requestBodyWithExistingUsername = createRequestBodyForCreatePatient(
        "Name",
        "Surname",
        "1989-02-20",
        "38902200198",
        username,
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBodyWithExistingUsername)
        .when()
        .post(URI_FOR_CREATE_PATIENT)
        .then()
        .expect(status().isConflict());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPatientShouldDisallowCreatingPatientWithExistingPersonalIdentificationNumber() {
    String birthDate = "1989-01-16";
    String personalIdentificationNumber = "38901160195";

    String requestBodyWithNonExistentPersonalIdNumber = createRequestBodyForCreatePatient(
        "Name",
        "Surname",
        birthDate,
        personalIdentificationNumber,
        "username1",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBodyWithNonExistentPersonalIdNumber)
        .post(URI_FOR_CREATE_PATIENT);

    String requestBodyWithExistingPersonalIdNumber = createRequestBodyForCreatePatient(
        "Name",
        "Surname",
        birthDate,
        personalIdentificationNumber,
        "username2",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBodyWithExistingPersonalIdNumber)
        .when()
        .post(URI_FOR_CREATE_PATIENT)
        .then()
        .expect(status().isConflict());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPatientShouldDisallowCreatingPatientWithBlankFirstName() {
    String requestBody = createRequestBodyForCreatePatient(
        "",
        "Surname",
        "1989-01-16",
        "38901160195",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PATIENT)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPatientShouldDisallowCreatingPatientWithBlankLastName() {
    String requestBody = createRequestBodyForCreatePatient(
        "Name",
        "",
        "1989-01-16",
        "38901160195",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PATIENT)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPatientShouldDisallowCreatingPatientWithBlankBirthDate() {
    String requestBody = createRequestBodyForCreatePatient(
        "Name",
        "Surname",
        "",
        "38901160195",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PATIENT)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPatientShouldDisallowCreatingPatientWithBlankPersonalIdentificationNumber() {
    String requestBody = createRequestBodyForCreatePatient(
        "Name",
        "Surname",
        "1989-01-16",
        "",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PATIENT)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPatientShouldDisallowCreatingPatientWithBlankUsername() {
    String requestBody = createRequestBodyForCreatePatient(
        "Name",
        "Surname",
        "1989-01-16",
        "38901160195",
        "",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PATIENT)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPatientShouldDisallowCreatingPatientWithBlankPassword() {
    String requestBody = createRequestBodyForCreatePatient(
        "Name",
        "Surname",
        "1989-01-16",
        "38901160195",
        "username",
        ""
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PATIENT)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPatientShouldDisallowCreatingPatientWithNullFirstName() {
    String requestBody = createRequestBodyForCreatePatient(
        null,
        "Surname",
        "1989-01-16",
        "38901160195",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PATIENT)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPatientShouldDisallowCreatingPatientWithNullLastName() {
    String requestBody = createRequestBodyForCreatePatient(
        "Name",
        null,
        "1989-01-16",
        "38901160195",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PATIENT)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPatientShouldDisallowCreatingPatientWithNullBirthDate() {
    String requestBody = createRequestBodyForCreatePatient(
        "Name",
        "Surname",
        null,
        "38901160195",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PATIENT)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPatientShouldDisallowCreatingPatientWithNullPersonalIdentificationNumber() {
    String requestBody = createRequestBodyForCreatePatient(
        "Name",
        "Surname",
        "1989-01-16",
        null,
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PATIENT)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPatientShouldDisallowCreatingPatientWithNullUsername() {
    String requestBody = createRequestBodyForCreatePatient(
        "Name",
        "Surname",
        "1989-01-16",
        "38901160195",
        null,
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PATIENT)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPatientShouldDisallowCreatingPatientWithNullPassword() {
    String requestBody = createRequestBodyForCreatePatient(
        "Name",
        "Surname",
        "1989-01-16",
        "38901160195",
        "username",
        null
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PATIENT)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPatientShouldDisallowCreatingPatientWithTooLongFirstName() {
    String requestBody = createRequestBodyForCreatePatient(
        "A" + RequestUtil.repeatText( "a", ValidationConstants.NAME_LENGTH_MAX),
        "Surname",
        "1989-01-16",
        "38901160195",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PATIENT)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPatientShouldDisallowCreatingPatientWithTooLongLastName() {
    String requestBody = createRequestBodyForCreatePatient(
        "Name",
        "A" + RequestUtil.repeatText( "a", ValidationConstants.NAME_LENGTH_MAX),
        "1989-01-16",
        "38901160195",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PATIENT)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPatientShouldDisallowCreatingPatientWithBirthDateEarlierThanMinimumDate() {
    String requestBody = createRequestBodyForCreatePatient(
        "Name",
        "Surname",
        "1899-01-16",
        "39901160195",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PATIENT)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPatientShouldDisallowCreatingPatientWithBirthDateInTheFuture() {
    String requestBody = createRequestBodyForCreatePatient(
        "Name",
        "Surname",
        "2018-02-02",
        "51802020195",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PATIENT)
        .then()
        .expect(status().isBadRequest());
  }

  @SuppressWarnings("checkstyle:linelength")
  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void
      createPatientShouldDisallowCreatingPatientWithPersonalIdNumberThatDoesNotMatchBirthDateDigits() {
    String requestBody = createRequestBodyForCreatePatient(
        "Name",
        "Surname",
        "1989-01-16",
        "38902160195",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PATIENT)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPatientShouldDisallowCreatingPatientWithTooLongUsername() {
    String requestBody = createRequestBodyForCreatePatient(
        "Name",
        "Surname",
        "1989-01-16",
        "38901160195",
        RequestUtil.repeatText("a", ValidationConstants.USERNAME_LENGTH_MAX + 1),
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PATIENT)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPatientShouldDisallowCreatingPatientWithTooShortUsername() {
    String requestBody = createRequestBodyForCreatePatient(
        "Name",
        "Surname",
        "1989-01-16",
        "38901160195",
        RequestUtil.repeatText("a", ValidationConstants.USERNAME_LENGTH_MIN - 1),
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PATIENT)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPatientShouldDisallowCreatingPatientWithTooLongPassword() {
    String requestBody = createRequestBodyForCreatePatient(
        "Name",
        "Surname",
        "1989-01-16",
        "38901160195",
        "username",
        RequestUtil.repeatText("a", ValidationConstants.PASSWORD_LENGTH_MAX + 1)
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PATIENT)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPatientShouldDisallowCreatingPatientWithTooShortPassword() {
    String requestBody = createRequestBodyForCreatePatient(
        "Name",
        "Surname",
        "1989-01-16",
        "38901160195",
        "username",
        RequestUtil.repeatText("a", ValidationConstants.PASSWORD_LENGTH_MIN - 1)
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PATIENT)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPatientShouldCreatePatientWithCorrectData() {
    String firstName = "Name";
    String lastName = "Surname";
    String personalIdentificationNumber = "38901160195";
    String username = "username";

    String requestBody = createRequestBodyForCreatePatient(
        firstName,
        lastName,
        "1989-01-16",
        personalIdentificationNumber,
        username,
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .post(URI_FOR_CREATE_PATIENT);

    Optional<Patient> newPatient = patientRepository
        .findAll()
        .stream()
        .filter(patient ->
            patient.getUsername().equals(username)
        ).findAny();

    boolean isPresent = newPatient.isPresent();
    assertThat(isPresent, equalTo(true));

    if (isPresent) {
      Patient patient = newPatient.get();

      assertThat(patient.getFirstName(), equalTo(firstName));
      assertThat(patient.getLastName(), equalTo(lastName));
      assertThat(patient.getPersonalIdentificationNumber(), equalTo(personalIdentificationNumber));
      assertThat(
          patient.getBirthDate(),
          equalTo(new GregorianCalendar(1989, Calendar.JANUARY, 16).getTime())
      );
      assertThat(patient.getRole(), equalTo(Role.PATIENT));
      assertThat(
          patient.getLastPasswordResetDate().getTime(),
          equalTo(TestDataConstants.CURRENT_TIME_FOR_TESTING.getTime())
      );
    }
  }

  @Test
  public void getPatientByIdShouldDenyAccessForUnauthenticatedUser() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .when()
        .get(getUriForGetPatientById(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID))
        .then()
        .expect(status().isUnauthorized());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void getPatientByIdShouldDenyAccessForAdministrator() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .when()
        .get(getUriForGetPatientById(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID))
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PATIENT_WITHOUT_MEDICAL_INFORMATION_USERNAME)
  public void getPatientByIdShouldDenyAccessForPatient() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .when()
        .get(getUriForGetPatientById(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID))
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PHARMACIST_USERNAME)
  public void getPatientByIdShouldDenyAccessForPharmacist() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .when()
        .get(getUriForGetPatientById(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID))
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITHOUT_PATIENTS_USERNAME)
  public void getPatientByIdShouldDenyAccessForDoctorWithPatientNotAssignedToHim() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .when()
        .get(getUriForGetPatientById(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID))
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void getPatientByIdShouldAllowAccessForDoctorWithPatientAssignedToHim() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .when()
        .get(getUriForGetPatientById(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID))
        .then()
        .expect(status().isOk());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void getPatientByIdShouldReturnPatientInformation() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .when()
        .get(getUriForGetPatientById(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID))
        .then()
        .body("id", equalTo(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID))
        .body("firstName", equalTo(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_FIRST_NAME))
        .body("lastName", equalTo(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_LAST_NAME))
        .body(
            "personalIdentificationNumber",
            equalTo(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_PERSONAL_ID_NUMBER)
        ).body("birthDate", equalTo(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_BIRTH_DATE));
  }

  @Test
  public void getPatientByPersonalIdNumberShouldDenyAccessForUnauthenticatedUser() {
    String requestBody = createRequestBodyForGetPatientByPersonalId(
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_PERSONAL_ID_NUMBER
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_GET_PATIENT_BY_PERSONAL_ID_NUMBER)
        .then()
        .expect(status().isUnauthorized());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void getPatientByPersonalIdNumberShouldDenyAccessForAdministrator() {
    String requestBody = createRequestBodyForGetPatientByPersonalId(
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_PERSONAL_ID_NUMBER
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_GET_PATIENT_BY_PERSONAL_ID_NUMBER)
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PATIENT_WITHOUT_MEDICAL_INFORMATION_USERNAME)
  public void getPatientByPersonalIdNumberShouldDenyAccessForPatient() {
    String requestBody = createRequestBodyForGetPatientByPersonalId(
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_PERSONAL_ID_NUMBER
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_GET_PATIENT_BY_PERSONAL_ID_NUMBER)
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PHARMACIST_USERNAME)
  public void getPatientByPersonalIdNumberShouldAllowAccessForPharmacist() {
    String requestBody = createRequestBodyForGetPatientByPersonalId(
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_PERSONAL_ID_NUMBER
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_GET_PATIENT_BY_PERSONAL_ID_NUMBER)
        .then()
        .expect(status().isOk());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITHOUT_PATIENTS_USERNAME)
  public void getPatientByPersonalIdNumberShouldAllowAccessForDoctorWithPatientNotAssignedToHim() {
    String requestBody = createRequestBodyForGetPatientByPersonalId(
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_PERSONAL_ID_NUMBER
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_GET_PATIENT_BY_PERSONAL_ID_NUMBER)
        .then()
        .expect(status().isOk());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void getPatientByPersonalIdNumberShouldAllowAccessForDoctorWithPatientAssignedToHim() {
    String requestBody = createRequestBodyForGetPatientByPersonalId(
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_PERSONAL_ID_NUMBER
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_GET_PATIENT_BY_PERSONAL_ID_NUMBER)
        .then()
        .expect(status().isOk());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void getPatientByPersonalIdNumberShouldReturnPatientInformation() {
    String requestBody = createRequestBodyForGetPatientByPersonalId(
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_PERSONAL_ID_NUMBER
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_GET_PATIENT_BY_PERSONAL_ID_NUMBER)
        .then()
        .body("id", equalTo(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID))
        .body("firstName", equalTo(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_FIRST_NAME))
        .body("lastName", equalTo(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_LAST_NAME))
        .body(
            "personalIdentificationNumber",
            equalTo(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_PERSONAL_ID_NUMBER)
        ).body("birthDate", equalTo(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_BIRTH_DATE));
  }

  @Test
  public void filterPatientsForAdministratorShouldDenyAccessForUnauthenticatedUser() {
    String requestBody = createRequestBodyForFilterPatientsForAdministrator(
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_FIRST_NAME,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_LAST_NAME,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_PERSONAL_ID_NUMBER,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME,
        1,
        1
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_FILTER_PATIENTS_FOR_ADMINISTRATOR)
        .then()
        .expect(status().isUnauthorized());
  }

  @Test
  @WithUserDetails(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME)
  public void filterPatientsForAdministratorShouldDenyAccessForPatient() {
    String requestBody = createRequestBodyForFilterPatientsForAdministrator(
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_FIRST_NAME,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_LAST_NAME,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_PERSONAL_ID_NUMBER,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME,
        1,
        1
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_FILTER_PATIENTS_FOR_ADMINISTRATOR)
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void filterPatientsForAdministratorShouldDenyAccessForDoctor() {
    String requestBody = createRequestBodyForFilterPatientsForAdministrator(
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_FIRST_NAME,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_LAST_NAME,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_PERSONAL_ID_NUMBER,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME,
        1,
        1
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_FILTER_PATIENTS_FOR_ADMINISTRATOR)
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PHARMACIST_USERNAME)
  public void filterPatientsForAdministratorShouldDenyAccessForPharmacist() {
    String requestBody = createRequestBodyForFilterPatientsForAdministrator(
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_FIRST_NAME,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_LAST_NAME,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_PERSONAL_ID_NUMBER,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME,
        1,
        1
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_FILTER_PATIENTS_FOR_ADMINISTRATOR)
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void filterPatientsForAdministratorShouldAllowAccessForAdministrator() {
    String requestBody = createRequestBodyForFilterPatientsForAdministrator(
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_FIRST_NAME,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_LAST_NAME,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_PERSONAL_ID_NUMBER,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME,
        1,
        1
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_FILTER_PATIENTS_FOR_ADMINISTRATOR)
        .then()
        .expect(status().isOk());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void filterPatientsForAdministratorShouldSuccessfullyFilterPatients() {
    String requestBody = createRequestBodyForFilterPatientsForAdministrator(
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_FIRST_NAME,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_LAST_NAME,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_PERSONAL_ID_NUMBER,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME,
        1,
        1
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_FILTER_PATIENTS_FOR_ADMINISTRATOR)
        .then()
        .body("elements.size()", equalTo(1))
        .body(
            "elements[0].firstName",
            equalTo(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_FIRST_NAME)
        ).body(
            "elements[0].lastName",
            equalTo(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_LAST_NAME)
        ).body(
            "elements[0].personalIdentificationNumber",
            equalTo(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_PERSONAL_ID_NUMBER)
        ).body(
            "elements[0].username",
            equalTo(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME)
        ).body("hasNext", equalTo(false));
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void filterPatientsForAdministratorShouldPerformPagination() {
    String requestBody = createRequestBodyForFilterPatientsForAdministrator(
        "",
        "",
        "",
        "",
        1,
        1
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_FILTER_PATIENTS_FOR_ADMINISTRATOR)
        .then()
        .body("elements.size()", equalTo(1))
        .body("hasNext", equalTo(true));
  }

  @Test
  public void filterPatientsForDoctorShouldDenyAccessForUnauthenticatedUser() {
    String requestBody = createRequestBodyForFilterPatientsForDoctor(
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_FIRST_NAME,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_LAST_NAME,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_PERSONAL_ID_NUMBER,
        "",
        1,
        1
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(getUriForFilterPatientsForDoctor(TestDataConstants.DOCTOR_WITH_PATIENTS_ID))
        .then()
        .expect(status().isUnauthorized());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void filterPatientsForDoctorShouldDenyAccessForAdministrator() {
    String requestBody = createRequestBodyForFilterPatientsForDoctor(
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_FIRST_NAME,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_LAST_NAME,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_PERSONAL_ID_NUMBER,
        "",
        1,
        1
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(getUriForFilterPatientsForDoctor(TestDataConstants.DOCTOR_WITH_PATIENTS_ID))
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PATIENT_WITHOUT_MEDICAL_INFORMATION_USERNAME)
  public void filterPatientsForDoctorShouldDenyAccessForPatient() {
    String requestBody = createRequestBodyForFilterPatientsForDoctor(
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_FIRST_NAME,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_LAST_NAME,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_PERSONAL_ID_NUMBER,
        "",
        1,
        1
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(getUriForFilterPatientsForDoctor(TestDataConstants.DOCTOR_WITH_PATIENTS_ID))
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PHARMACIST_USERNAME)
  public void filterPatientsForDoctorShouldDenyAccessForPharmacist() {
    String requestBody = createRequestBodyForFilterPatientsForDoctor(
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_FIRST_NAME,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_LAST_NAME,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_PERSONAL_ID_NUMBER,
        "",
        1,
        1
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(getUriForFilterPatientsForDoctor(TestDataConstants.DOCTOR_WITH_PATIENTS_ID))
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITHOUT_PATIENTS_USERNAME)
  public void filterPatientsForDoctorShouldDenyDoctorToFilterAnotherDoctorsPatientList() {
    String requestBody = createRequestBodyForFilterPatientsForDoctor(
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_FIRST_NAME,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_LAST_NAME,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_PERSONAL_ID_NUMBER,
        "",
        1,
        1
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(getUriForFilterPatientsForDoctor(TestDataConstants.DOCTOR_WITH_PATIENTS_ID))
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void filterPatientsForDoctorShouldAllowAccessForDoctorToFilterHisOwnPatientList() {
    String requestBody = createRequestBodyForFilterPatientsForDoctor(
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_FIRST_NAME,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_LAST_NAME,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_PERSONAL_ID_NUMBER,
        "",
        1,
        1
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(getUriForFilterPatientsForDoctor(TestDataConstants.DOCTOR_WITH_PATIENTS_ID))
        .then()
        .expect(status().isOk());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void filterPatientsForDoctorShouldSuccessfullyFilterPatients() {
    String requestBody = createRequestBodyForFilterPatientsForDoctor(
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_FIRST_NAME,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_LAST_NAME,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_PERSONAL_ID_NUMBER,
        "A54.42",
        1,
        1
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(getUriForFilterPatientsForDoctor(TestDataConstants.DOCTOR_WITH_PATIENTS_ID))
        .then()
        .body("elements.size()", equalTo(1))
        .body(
            "elements[0].firstName",
            equalTo(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_FIRST_NAME)
        ).body(
        "elements[0].lastName",
            equalTo(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_LAST_NAME)
        ).body(
            "elements[0].personalIdentificationNumber",
            equalTo(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_PERSONAL_ID_NUMBER)
        ).body(
            "elements[0].birthDate",
            equalTo(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_BIRTH_DATE)
        ).body("hasNext", equalTo(false));
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void filterPatientsForDoctorShouldPerformPagination() {
    String requestBody = createRequestBodyForFilterPatientsForDoctor(
        "",
        "",
        "",
        "",
        1,
        1
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(getUriForFilterPatientsForDoctor(TestDataConstants.DOCTOR_WITH_PATIENTS_ID))
        .then()
        .body("elements.size()", equalTo(1))
        .body("hasNext", equalTo(true));
  }

  @Test
  public void downloadPatientCsvShouldDenyAccessForUnauthenticatedUser() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .when()
        .get(getUriForDownloadPatientCsv(TestDataConstants.DOCTOR_WITH_PATIENTS_ID))
        .then()
        .expect(status().isUnauthorized());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void downloadPatientCsvShouldDenyAccessForAdministrator() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .when()
        .get(getUriForDownloadPatientCsv(TestDataConstants.DOCTOR_WITH_PATIENTS_ID))
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PATIENT_WITHOUT_MEDICAL_INFORMATION_USERNAME)
  public void downloadPatientCsvShouldDenyAccessForPatient() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .when()
        .get(getUriForDownloadPatientCsv(TestDataConstants.DOCTOR_WITH_PATIENTS_ID))
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PHARMACIST_USERNAME)
  public void downloadPatientCsvShouldDenyAccessForPharmacist() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .when()
        .get(getUriForDownloadPatientCsv(TestDataConstants.DOCTOR_WITH_PATIENTS_ID))
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITHOUT_PATIENTS_USERNAME)
  public void downloadPatientCsvShouldDenyDoctorToDownloadAnotherDoctorsPatientList() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .when()
        .get(getUriForDownloadPatientCsv(TestDataConstants.DOCTOR_WITH_PATIENTS_ID))
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void downloadPatientCsvShouldAllowDoctorToDownloadHisOwnPatientList() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .when()
        .get(getUriForDownloadPatientCsv(TestDataConstants.DOCTOR_WITH_PATIENTS_ID))
        .then()
        .expect(status().isOk());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void downloadPatientCsvShouldSuccessfullyDownloadPatientListCsv() {
    String response = RestAssuredMockMvc.given().mockMvc(mvc)
        .when()
        .get(getUriForDownloadPatientCsv(TestDataConstants.DOCTOR_WITH_PATIENTS_ID))
        .then()
        .contentType("text/csv")
        .extract()
        .asString();

    assertThat(
        response,
        containsString(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_FIRST_NAME)
    );
    assertThat(
        response,
        containsString(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_LAST_NAME)
    );
    assertThat(
        response,
        containsString(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_PERSONAL_ID_NUMBER)
    );
    assertThat(
        response,
        containsString(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_BIRTH_DATE)
    );

    assertThat(response, containsString("Gintaras"));
    assertThat(response, containsString("Užmatas"));
    assertThat(response, containsString("38008121234"));
    assertThat(response, containsString("1980-08-12"));

    assertThat(response, containsString("Edita"));
    assertThat(response, containsString("Ričkatienė"));
    assertThat(response, containsString("49410081111"));
    assertThat(response, containsString("1994-10-08"));
  }
}
