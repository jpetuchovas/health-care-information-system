package com.justinaspetuchovas.health.controller;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import com.justinaspetuchovas.health.common.TimeProvider;
import com.justinaspetuchovas.health.model.user.Role;
import com.justinaspetuchovas.health.model.user.doctor.Doctor;
import com.justinaspetuchovas.health.model.user.patient.Patient;
import com.justinaspetuchovas.health.repository.DoctorRepository;
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
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
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
public class DoctorControllerIT {
  private static final String URI_FOR_CREATE_DOCTOR = "/api/doctors";
  private static final String URI_FOR_FILTER_DOCTORS = "/api/doctors/filter/admin";
  private MockMvc mvc;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private DoctorRepository doctorRepository;

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

  private String createRequestBodyForCreateDoctor(
      String firstName,
      String lastName,
      String specialization,
      String username,
      String password
  ) {
    return new JSONObject()
        .put("firstName", RequestUtil.replaceNullWithJsonNull(firstName))
        .put("lastName", RequestUtil.replaceNullWithJsonNull(lastName))
        .put("specialization", RequestUtil.replaceNullWithJsonNull(specialization))
        .put("username", RequestUtil.replaceNullWithJsonNull(username))
        .put("password", RequestUtil.replaceNullWithJsonNull(password))
        .toString();
  }

  private String createRequestBodyForFilterDoctors(
      String firstName,
      String lastName,
      String specialization,
      String username,
      int pageNumber,
      int pageSize
  ) {
    return new JSONObject()
        .put("firstName", firstName)
        .put("lastName", lastName)
        .put("specialization", specialization)
        .put("username", username)
        .put("pageNumber", pageNumber)
        .put("pageSize", pageSize)
        .toString();
  }

  private String getUriForAssignPatient(String doctorId, String patientId) {
    return MessageFormat.format("/api/doctors/{0}/patients/{1}", doctorId, patientId);
  }

  @Test
  public void createDoctorShouldDenyAccessForUnauthenticatedUser() {
    String requestBody = createRequestBodyForCreateDoctor(
        "Name",
        "Surname",
        "Surgeon",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_DOCTOR)
        .then()
        .expect(status().isUnauthorized());
  }

  @Test
  @WithUserDetails(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME)
  public void createDoctorShouldDenyAccessForPatient() {
    String requestBody = createRequestBodyForCreateDoctor(
        "Name",
        "Surname",
        "Surgeon",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_DOCTOR)
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void createDoctorShouldDenyAccessForDoctor() {
    String requestBody = createRequestBodyForCreateDoctor(
        "Name",
        "Surname",
        "Surgeon",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_DOCTOR)
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PHARMACIST_USERNAME)
  public void createDoctorShouldDenyAccessForPharmacist() {
    String requestBody = createRequestBodyForCreateDoctor(
        "Name",
        "Surname",
        "Surgeon",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_DOCTOR)
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createDoctorShouldAllowAccessForAdministrator() {
    String requestBody = createRequestBodyForCreateDoctor(
        "Name",
        "Surname",
        "Surgeon",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_DOCTOR)
        .then()
        .expect(status().isCreated());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createDoctorShouldDisallowCreatingDoctorWithExistingUsername() {
    String username = "username";

    String requestBodyWithNonExistentUsername = createRequestBodyForCreateDoctor(
        "Name",
        "Surname",
        "Surgeon",
        username,
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBodyWithNonExistentUsername)
        .post(URI_FOR_CREATE_DOCTOR);

    String requestBodyWithExistingUsername = createRequestBodyForCreateDoctor(
        "Name",
        "Surname",
        "Surgeon",
        username,
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBodyWithExistingUsername)
        .when()
        .post(URI_FOR_CREATE_DOCTOR)
        .then()
        .expect(status().isConflict());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createDoctorShouldDisallowCreatingDoctorWithBlankFirstName() {
    String requestBody = createRequestBodyForCreateDoctor(
        "",
        "Surname",
        "Surgeon",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_DOCTOR)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createDoctorShouldDisallowCreatingDoctorWithBlankLastName() {
    String requestBody = createRequestBodyForCreateDoctor(
        "Name",
        "",
        "Surgeon",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_DOCTOR)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createDoctorShouldDisallowCreatingDoctorWithBlankSpecialization() {
    String requestBody = createRequestBodyForCreateDoctor(
        "Name",
        "Surname",
        "",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_DOCTOR)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createDoctorShouldDisallowCreatingDoctorWithBlankUsername() {
    String requestBody = createRequestBodyForCreateDoctor(
        "Name",
        "Surname",
        "Surgeon",
        "",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_DOCTOR)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createDoctorShouldDisallowCreatingDoctorWithBlankPassword() {
    String requestBody = createRequestBodyForCreateDoctor(
        "Name",
        "Surname",
        "Surgeon",
        "username",
        ""
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_DOCTOR)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createDoctorShouldDisallowCreatingDoctorWithNullFirstName() {
    String requestBody = createRequestBodyForCreateDoctor(
        null,
        "Surname",
        "Surgeon",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_DOCTOR)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createDoctorShouldDisallowCreatingDoctorWithNullLastName() {
    String requestBody = createRequestBodyForCreateDoctor(
        "Name",
        null,
        "Surgeon",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_DOCTOR)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createDoctorShouldDisallowCreatingDoctorWithNullSpecialization() {
    String requestBody = createRequestBodyForCreateDoctor(
        "Name",
        "Surname",
        null,
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_DOCTOR)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createDoctorShouldDisallowCreatingDoctorWithNullUsername() {
    String requestBody = createRequestBodyForCreateDoctor(
        "Name",
        "Surname",
        "Surgeon",
        null,
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_DOCTOR)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createDoctorShouldDisallowCreatingDoctorWithNullPassword() {
    String requestBody = createRequestBodyForCreateDoctor(
        "Name",
        "Surname",
        "Surgeon",
        "username",
        null
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_DOCTOR)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createDoctorShouldCreateDoctorWithCorrectData() {
    String firstName = "Name";
    String lastName = "Surname";
    String specialization = "Surgeon";
    String username = "username";

    String requestBody = createRequestBodyForCreateDoctor(
        firstName,
        lastName,
        specialization,
        username,
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .post(URI_FOR_CREATE_DOCTOR);

    Optional<Doctor> newDoctor = doctorRepository
        .findAll()
        .stream()
        .filter(doctor -> doctor.getUsername().equals(username))
        .findAny();

    boolean isPresent = newDoctor.isPresent();
    assertThat(isPresent, equalTo(true));

    if (isPresent) {
      Doctor doctor = newDoctor.get();

      assertThat(doctor.getFirstName(), equalTo(firstName));
      assertThat(doctor.getLastName(), equalTo(lastName));
      assertThat(doctor.getSpecialization(), equalTo(specialization));
      assertThat(doctor.getRole(), equalTo(Role.DOCTOR));
      assertThat(
          doctor.getLastPasswordResetDate().getTime(),
          equalTo(TestDataConstants.CURRENT_TIME_FOR_TESTING.getTime())
      );
    }
  }

  @Test
  public void filterDoctorsShouldDenyAccessForUnauthenticatedUser() {
    String requestBody = createRequestBodyForFilterDoctors(
        TestDataConstants.DOCTOR_WITH_PATIENTS_FIRST_NAME,
        TestDataConstants.DOCTOR_WITH_PATIENTS_LAST_NAME,
        TestDataConstants.DOCTOR_WITH_PATIENTS_SPECIALIZATION,
        TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME,
        1,
        1
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_FILTER_DOCTORS)
        .then()
        .expect(status().isUnauthorized());
  }

  @Test
  @WithUserDetails(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME)
  public void filterDoctorsShouldDenyAccessForPatient() {
    String requestBody = createRequestBodyForFilterDoctors(
        TestDataConstants.DOCTOR_WITH_PATIENTS_FIRST_NAME,
        TestDataConstants.DOCTOR_WITH_PATIENTS_LAST_NAME,
        TestDataConstants.DOCTOR_WITH_PATIENTS_SPECIALIZATION,
        TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME,
        1,
        1
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_FILTER_DOCTORS)
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void filterDoctorsShouldDenyAccessForDoctor() {
    String requestBody = createRequestBodyForFilterDoctors(
        TestDataConstants.DOCTOR_WITH_PATIENTS_FIRST_NAME,
        TestDataConstants.DOCTOR_WITH_PATIENTS_LAST_NAME,
        TestDataConstants.DOCTOR_WITH_PATIENTS_SPECIALIZATION,
        TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME,
        1,
        1
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_FILTER_DOCTORS)
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PHARMACIST_USERNAME)
  public void filterDoctorsShouldDenyAccessForPharmacist() {
    String requestBody = createRequestBodyForFilterDoctors(
        TestDataConstants.DOCTOR_WITH_PATIENTS_FIRST_NAME,
        TestDataConstants.DOCTOR_WITH_PATIENTS_LAST_NAME,
        TestDataConstants.DOCTOR_WITH_PATIENTS_SPECIALIZATION,
        TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME,
        1,
        1
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_FILTER_DOCTORS)
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void filterDoctorsShouldAllowAccessForAdministrator() {
    String requestBody = createRequestBodyForFilterDoctors(
        TestDataConstants.DOCTOR_WITH_PATIENTS_FIRST_NAME,
        TestDataConstants.DOCTOR_WITH_PATIENTS_LAST_NAME,
        TestDataConstants.DOCTOR_WITH_PATIENTS_SPECIALIZATION,
        TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME,
        1,
        1
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_FILTER_DOCTORS)
        .then()
        .expect(status().isOk());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void filterDoctorsShouldSuccessfullyFilterDoctors() {
    String requestBody = createRequestBodyForFilterDoctors(
        TestDataConstants.DOCTOR_WITH_PATIENTS_FIRST_NAME,
        TestDataConstants.DOCTOR_WITH_PATIENTS_LAST_NAME,
        TestDataConstants.DOCTOR_WITH_PATIENTS_SPECIALIZATION,
        TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME,
        1,
        1
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_FILTER_DOCTORS)
        .then()
        .body("elements.size()", equalTo(1))
        .body("elements[0].firstName", equalTo(TestDataConstants.DOCTOR_WITH_PATIENTS_FIRST_NAME))
        .body("elements[0].lastName", equalTo(TestDataConstants.DOCTOR_WITH_PATIENTS_LAST_NAME))
        .body(
            "elements[0].specialization",
            equalTo(TestDataConstants.DOCTOR_WITH_PATIENTS_SPECIALIZATION)
        ).body("elements[0].username", equalTo(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME))
        .body("hasNext", equalTo(false));
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void filterDoctorsShouldPerformPagination() {
    String requestBody = createRequestBodyForFilterDoctors(
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
        .post(URI_FOR_FILTER_DOCTORS)
        .then()
        .body("elements.size()", equalTo(1))
        .body("hasNext", equalTo(true));
  }

  @Test
  public void assignPatientShouldDenyAccessForUnauthenticatedUser() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .when()
        .put(
            getUriForAssignPatient(
                TestDataConstants.DOCTOR_WITHOUT_PATIENTS_ID,
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .expect(status().isUnauthorized());
  }

  @Test
  @WithUserDetails(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME)
  public void assignPatientShouldDenyAccessForPatient() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .when()
        .put(
            getUriForAssignPatient(
                TestDataConstants.DOCTOR_WITHOUT_PATIENTS_ID,
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void assignPatientShouldDenyAccessForDoctor() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .when()
        .put(
            getUriForAssignPatient(
                TestDataConstants.DOCTOR_WITHOUT_PATIENTS_ID,
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PHARMACIST_USERNAME)
  public void assignPatientShouldDenyAccessForPharmacist() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .when()
        .put(
            getUriForAssignPatient(
                TestDataConstants.DOCTOR_WITHOUT_PATIENTS_ID,
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void assignPatientShouldAllowAccessForAdministrator() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .when()
        .put(
            getUriForAssignPatient(
                TestDataConstants.DOCTOR_WITHOUT_PATIENTS_ID,
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .expect(status().isNoContent());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void assignPatientShouldDisallowAssigningNonExistentPatientToDoctor() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .when()
        .put(
            getUriForAssignPatient(
                TestDataConstants.DOCTOR_WITHOUT_PATIENTS_ID,
                TestDataConstants.NON_EXISTENT_ID
            )
        ).then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void assignPatientShouldDisallowAssigningPatientToNonExistentDoctor() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .when()
        .put(
            getUriForAssignPatient(
                TestDataConstants.NON_EXISTENT_ID,
                TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
            )
        ).then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void assignPatientShouldSuccessfullyAssignPatientToDoctor() {
    RestAssuredMockMvc.given().mockMvc(mvc).put(getUriForAssignPatient(
        TestDataConstants.DOCTOR_WITHOUT_PATIENTS_ID,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID
    ));

    Patient patient = patientRepository
        .findOne(UUID.fromString(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID));

    assertThat(
        patient.getDoctor().getId().toString(),
        equalTo(TestDataConstants.DOCTOR_WITHOUT_PATIENTS_ID)
    );
  }
}
