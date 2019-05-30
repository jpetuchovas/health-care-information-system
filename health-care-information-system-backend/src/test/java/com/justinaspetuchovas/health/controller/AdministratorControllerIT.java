package com.justinaspetuchovas.health.controller;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import com.justinaspetuchovas.health.common.TimeProvider;
import com.justinaspetuchovas.health.common.ValidationConstants;
import com.justinaspetuchovas.health.model.user.Role;
import com.justinaspetuchovas.health.model.user.administrator.Administrator;
import com.justinaspetuchovas.health.repository.UserRepository;
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
public class AdministratorControllerIT {
  private static final String URI_FOR_CREATE_ADMINISTRATOR = "/api/administrators";
  private MockMvc mvc;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private UserRepository userRepository;

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

  private String createRequestBodyForCreateAdministrator(
      String firstName,
      String lastName,
      String username,
      String password
  ) {
    return new JSONObject()
        .put("firstName", RequestUtil.replaceNullWithJsonNull(firstName))
        .put("lastName", RequestUtil.replaceNullWithJsonNull(lastName))
        .put("username", RequestUtil.replaceNullWithJsonNull(username))
        .put("password", RequestUtil.replaceNullWithJsonNull(password))
        .toString();
  }

  @Test
  public void createAdministratorShouldDenyAccessForUnauthenticatedUser() {
    String requestBody = createRequestBodyForCreateAdministrator(
        "Name",
        "Surname",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_ADMINISTRATOR)
        .then()
        .expect(status().isUnauthorized());
  }

  @Test
  @WithUserDetails(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME)
  public void createAdministratorShouldDenyAccessForPatient() {
    String requestBody = createRequestBodyForCreateAdministrator(
        "Name",
        "Surname",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_ADMINISTRATOR)
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void createAdministratorShouldDenyAccessForDoctor() {
    String requestBody = createRequestBodyForCreateAdministrator(
        "Name",
        "Surname",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_ADMINISTRATOR)
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.PHARMACIST_USERNAME)
  public void createAdministratorShouldDenyAccessForPharmacist() {
    String requestBody = createRequestBodyForCreateAdministrator(
        "Name",
        "Surname",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_ADMINISTRATOR)
        .then()
        .expect(status().isForbidden());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createAdministratorShouldAllowAccessForAdministrator() {
    String requestBody = createRequestBodyForCreateAdministrator(
        "Name",
        "Surname",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_ADMINISTRATOR)
        .then()
        .expect(status().isCreated());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createAdministratorShouldDisallowCreatingAdministratorWithExistingUsername() {
    String username = "username";

    String requestBodyWithNonExistentUsername = createRequestBodyForCreateAdministrator(
        "Name",
        "Surname",
        username,
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBodyWithNonExistentUsername)
        .post(URI_FOR_CREATE_ADMINISTRATOR);

    String requestBodyWithExistingUsername = createRequestBodyForCreateAdministrator(
        "Name",
        "Surname",
        username,
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBodyWithExistingUsername)
        .when()
        .post(URI_FOR_CREATE_ADMINISTRATOR)
        .then()
        .expect(status().isConflict());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createAdministratorShouldDisallowCreatingAdministratorWithBlankFirstName() {
    String requestBody = createRequestBodyForCreateAdministrator(
        "",
        "Surname",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_ADMINISTRATOR)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createAdministratorShouldDisallowCreatingAdministratorWithBlankLastName() {
    String requestBody = createRequestBodyForCreateAdministrator(
        "Name",
        "",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_ADMINISTRATOR)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createAdministratorShouldDisallowCreatingAdministratorWithBlankUsername() {
    String requestBody = createRequestBodyForCreateAdministrator(
        "Name",
        "Surname",
        "",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_ADMINISTRATOR)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createAdministratorShouldDisallowCreatingAdministratorWithBlankPassword() {
    String requestBody = createRequestBodyForCreateAdministrator(
        "Name",
        "Surname",
        "username",
        ""
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_ADMINISTRATOR)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createAdministratorShouldDisallowCreatingAdministratorWithNullFirstName() {
    String requestBody = createRequestBodyForCreateAdministrator(
        null,
        "Surname",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_ADMINISTRATOR)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createAdministratorShouldDisallowCreatingAdministratorWithNullLastName() {
    String requestBody = createRequestBodyForCreateAdministrator(
        "Name",
        null,
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_ADMINISTRATOR)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createAdministratorShouldDisallowCreatingAdministratorWithNullUsername() {
    String requestBody = createRequestBodyForCreateAdministrator(
        "Name",
        "Surname",
        null,
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_ADMINISTRATOR)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createAdministratorShouldDisallowCreatingAdministratorWithNullPassword() {
    String requestBody = createRequestBodyForCreateAdministrator(
        "Name",
        "Surname",
        "username",
        null
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_ADMINISTRATOR)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createAdministratorShouldDisallowCreatingAdministratorWithTooLongFirstName() {
    String requestBody = createRequestBodyForCreateAdministrator(
        "A" + RequestUtil.repeatText( "a", ValidationConstants.NAME_LENGTH_MAX),
        "Surname",
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_ADMINISTRATOR)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createAdministratorShouldDisallowCreatingAdministratorWithTooLongLastName() {
    String requestBody = createRequestBodyForCreateAdministrator(
        "Name",
        "A" + RequestUtil.repeatText( "a", ValidationConstants.NAME_LENGTH_MAX),
        "username",
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_ADMINISTRATOR)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createAdministratorShouldDisallowCreatingAdministratorWithTooLongUsername() {
    String requestBody = createRequestBodyForCreateAdministrator(
        "Name",
        "Surname",
        RequestUtil.repeatText("a", ValidationConstants.USERNAME_LENGTH_MAX + 1),
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_ADMINISTRATOR)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createAdministratorShouldDisallowCreatingAdministratorWithTooShortUsername() {
    String requestBody = createRequestBodyForCreateAdministrator(
        "Name",
        "Surname",
        RequestUtil.repeatText("a", ValidationConstants.USERNAME_LENGTH_MIN - 1),
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_ADMINISTRATOR)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createAdministratorShouldDisallowCreatingAdministratorWithTooLongPassword() {
    String requestBody = createRequestBodyForCreateAdministrator(
        "Name",
        "Surname",
        "username",
        RequestUtil.repeatText("a", ValidationConstants.PASSWORD_LENGTH_MAX + 1)
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_ADMINISTRATOR)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createAdministratorShouldDisallowCreatingAdministratorWithTooShortPassword() {
    String requestBody = createRequestBodyForCreateAdministrator(
        "Name",
        "Surname",
        "username",
        RequestUtil.repeatText("a", ValidationConstants.PASSWORD_LENGTH_MIN - 1)
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_ADMINISTRATOR)
        .then()
        .expect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void createDoctorShouldCreateDoctorWithCorrectData() {
    String firstName = "Name";
    String lastName = "Surname";
    String username = "username";

    String requestBody = createRequestBodyForCreateAdministrator(
        firstName,
        lastName,
        username,
        "password"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .post(URI_FOR_CREATE_ADMINISTRATOR);

    Administrator newAdministrator = (Administrator) userRepository.findByUsername(username);

    boolean isPresent = newAdministrator != null;
    assertThat(isPresent, equalTo(true));

    if (isPresent) {
      assertThat(newAdministrator.getFirstName(), equalTo(firstName));
      assertThat(newAdministrator.getLastName(), equalTo(lastName));
      assertThat(newAdministrator.getRole(), equalTo(Role.ADMIN));
      assertThat(
          newAdministrator.getLastPasswordResetDate().getTime(),
          equalTo(TestDataConstants.CURRENT_TIME_FOR_TESTING.getTime())
      );
    }
  }
}
