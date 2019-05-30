package com.justinaspetuchovas.health.controller;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import com.justinaspetuchovas.health.common.TimeProvider;
import com.justinaspetuchovas.health.model.user.Role;
import com.justinaspetuchovas.health.model.user.pharmacist.Pharmacist;
import com.justinaspetuchovas.health.repository.PharmacistRepository;
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

import java.util.Optional;

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
public class PharmacistControllerIT {
  private static final String URI_FOR_CREATE_PHARMACIST = "/api/pharmacists";
  private MockMvc mvc;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private PharmacistRepository pharmacistRepository;

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

  private String createRequestBodyForCreatePharmacist(
      String firstName,
      String lastName,
      String workplace,
      String username,
      String password
  ) {
    return new JSONObject()
        .put("firstName", RequestUtil.replaceNullWithJsonNull(firstName))
        .put("lastName", RequestUtil.replaceNullWithJsonNull(lastName))
        .put("workplace", RequestUtil.replaceNullWithJsonNull(workplace))
        .put("username", RequestUtil.replaceNullWithJsonNull(username))
        .put("password", RequestUtil.replaceNullWithJsonNull(password))
        .toString();
  }

  @Test
  public void createPharmacistShouldDenyAccessForUnauthenticatedUser() {
    String requestBody = createRequestBodyForCreatePharmacist(
        "Name",
        "Surname",
        "UAB Workplace",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PHARMACIST)
        .then()
        .expect(status().isUnauthorized());
  }

  @Test
  @WithUserDetails(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME)
  public void createPharmacistShouldDenyAccessForPatient() {
    String requestBody = createRequestBodyForCreatePharmacist(
        "Name",
        "Surname",
        "UAB Workplace",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PHARMACIST)
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void createPharmacistShouldDenyAccessForDoctor() {
    String requestBody = createRequestBodyForCreatePharmacist(
        "Name",
        "Surname",
        "UAB Workplace",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PHARMACIST)
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PHARMACIST_USERNAME)
  public void createPharmacistShouldDenyAccessForPharmacist() {
    String requestBody = createRequestBodyForCreatePharmacist(
        "Name",
        "Surname",
        "UAB Workplace",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PHARMACIST)
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPharmacistShouldAllowAccessForAdministrator() {
    String requestBody = createRequestBodyForCreatePharmacist(
        "Name",
        "Surname",
        "UAB Workplace",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PHARMACIST)
        .then()
        .expect(status().isCreated());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPharmacistShouldDisallowCreatingPharmacistWithExistingUsername() {
    String username = "username";

    String requestBodyWithNonExistentUsername = createRequestBodyForCreatePharmacist(
        "Name",
        "Surname",
        "UAB Workplace",
        username,
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBodyWithNonExistentUsername)
        .post(URI_FOR_CREATE_PHARMACIST);

    String requestBodyWithExistingUsername = createRequestBodyForCreatePharmacist(
        "Name",
        "Surname",
        "UAB Workplace",
        username,
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBodyWithExistingUsername)
        .when()
        .post(URI_FOR_CREATE_PHARMACIST)
        .then()
        .expect(status().isConflict());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPharmacistShouldDisallowCreatingPharmacistWithBlankFirstName() {
    String requestBody = createRequestBodyForCreatePharmacist(
        "",
        "Surname",
        "UAB Workplace",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PHARMACIST)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPharmacistShouldDisallowCreatingPharmacistWithBlankLastName() {
    String requestBody = createRequestBodyForCreatePharmacist(
        "Name",
        "",
        "UAB Workplace",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PHARMACIST)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPharmacistShouldDisallowCreatingPharmacistWithBlankWorkplace() {
    String requestBody = createRequestBodyForCreatePharmacist(
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
        .post(URI_FOR_CREATE_PHARMACIST)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPharmacistShouldDisallowCreatingPharmacistWithBlankUsername() {
    String requestBody = createRequestBodyForCreatePharmacist(
        "Name",
        "Surname",
        "UAB Workplace",
        "",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PHARMACIST)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPharmacistShouldDisallowCreatingPharmacistWithBlankPassword() {
    String requestBody = createRequestBodyForCreatePharmacist(
        "Name",
        "Surname",
        "UAB Workplace",
        "username",
        ""
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PHARMACIST)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPharmacistShouldDisallowCreatingPharmacistWithNullFirstName() {
    String requestBody = createRequestBodyForCreatePharmacist(
        null,
        "Surname",
        "UAB Workplace",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PHARMACIST)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPharmacistShouldDisallowCreatingPharmacistWithNullLastName() {
    String requestBody = createRequestBodyForCreatePharmacist(
        "Name",
        null,
        "UAB Workplace",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PHARMACIST)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPharmacistShouldDisallowCreatingPharmacistWithNullWorkplace() {
    String requestBody = createRequestBodyForCreatePharmacist(
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
        .post(URI_FOR_CREATE_PHARMACIST)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPharmacistShouldDisallowCreatingPharmacistWithNullUsername() {
    String requestBody = createRequestBodyForCreatePharmacist(
        "Name",
        "Surname",
        "UAB Workplace",
        null,
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PHARMACIST)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createPharmacistShouldDisallowCreatingPharmacistWithNullPassword() {
    String requestBody = createRequestBodyForCreatePharmacist(
        "Name",
        "Surname",
        "UAB Workplace",
        "username",
        null
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_PHARMACIST)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createDoctorShouldCreateDoctorWithCorrectData() {
    String firstName = "Name";
    String lastName = "Surname";
    String workplace = "UAB Workplace";
    String username = "username";

    String requestBody = createRequestBodyForCreatePharmacist(
        firstName,
        lastName,
        workplace,
        username,
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .post(URI_FOR_CREATE_PHARMACIST);

    Optional<Pharmacist> newPharmacist = pharmacistRepository
        .findAll()
        .stream()
        .filter(pharmacist -> pharmacist.getUsername().equals(username))
        .findAny();

    boolean isPresent = newPharmacist.isPresent();
    assertThat(isPresent, equalTo(true));

    if (isPresent) {
      Pharmacist pharmacist = newPharmacist.get();

      assertThat(pharmacist.getFirstName(), equalTo(firstName));
      assertThat(pharmacist.getLastName(), equalTo(lastName));
      assertThat(pharmacist.getWorkplace(), equalTo(workplace));
      assertThat(pharmacist.getRole(), equalTo(Role.PHARMACIST));
      assertThat(
          pharmacist.getLastPasswordResetDate().getTime(),
          equalTo(TestDataConstants.CURRENT_TIME_FOR_TESTING.getTime())
      );
    }
  }
}
