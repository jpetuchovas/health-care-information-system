package com.justinaspetuchovas.health.controller;

import io.restassured.http.ContentType;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasKey;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

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
public class AuthenticationControllerIT {
  private static final String URI_FOR_CREATE_AUTHENTICATION_TOKEN = "/api/login";
  private static final String URI_FOR_REFRESH_AUTHENTICATION_TOKEN = "/api/refresh";
  private static final String URI_FOR_CHANGE_PASSWORD = "/api/password-change";
  private static final String JWT_HEADER_START = "Bearer ";
  private MockMvc mvc;

  @Value("${jwt.header}")
  private String jwtHeader;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Before
  public void setup() {
    mvc = MockMvcBuilders
        .webAppContextSetup(webApplicationContext)
        .apply(springSecurity())
        .build();
  }

  private String createRequestBodyForCreateAuthenticationToken(String username, String password) {
    return new JSONObject()
        .put("username", username)
        .put("password", password)
        .toString();
  }

  private String createRequestBodyForChangePassword(String oldPassword, String newPassword) {
    return new JSONObject()
        .put("oldPassword", oldPassword)
        .put("newPassword", newPassword)
        .toString();
  }

  @Test
  public void createAuthenticationTokenShouldAllowAccessForAllUsers() {
    String requestBody = createRequestBodyForCreateAuthenticationToken(
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_PASSWORD
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_AUTHENTICATION_TOKEN)
        .then()
        .expect(status().isOk());
  }

  @Test
  public void createAuthenticationTokenShouldDenyAccessForNonExistentUser() {
    String requestBody = createRequestBodyForCreateAuthenticationToken(
        "nonExistentUsername",
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_PASSWORD
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_AUTHENTICATION_TOKEN)
        .then()
        .expect(status().isUnauthorized());
  }

  @Test
  public void createAuthenticationTokenShouldDenyAccessWhenIncorrectPasswordIsProvided() {
    String requestBody = createRequestBodyForCreateAuthenticationToken(
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME,
        "incorrectPassword"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_AUTHENTICATION_TOKEN)
        .then()
        .expect(status().isUnauthorized());
  }

  @Test
  @WithUserDetails(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME)
  public void createAuthenticationTokenShouldReturnNewValidToken() {
    String requestBody = createRequestBodyForCreateAuthenticationToken(
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_PASSWORD
    );

    String newToken = RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_AUTHENTICATION_TOKEN)
        .then()
        .body("$", hasKey("token"))
        .extract()
        .path("token");

    RestAssuredMockMvc.given().mockMvc(mvc)
        .header(jwtHeader, JWT_HEADER_START + newToken)
        .when()
        .get(URI_FOR_REFRESH_AUTHENTICATION_TOKEN)
        .then()
        .expect(status().isOk());
  }

  @Test
  public void refreshAuthenticationTokenShouldDenyAccessForUnauthenticatedUser() {
    RestAssuredMockMvc.given().mockMvc(mvc)
        .when()
        .get(URI_FOR_REFRESH_AUTHENTICATION_TOKEN)
        .then()
        .expect(status().isUnauthorized());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void refreshAuthenticationTokenShouldAllowAccessForAdministrator() {
    String requestBody = createRequestBodyForCreateAuthenticationToken(
        TestDataConstants.ADMINISTRATOR_USERNAME,
        TestDataConstants.ADMINISTRATOR_PASSWORD
    );

    String token = RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_AUTHENTICATION_TOKEN)
        .then()
        .body("$", hasKey("token"))
        .extract()
        .path("token");

    RestAssuredMockMvc.given().mockMvc(mvc)
        .header(jwtHeader, JWT_HEADER_START + token)
        .when()
        .get(URI_FOR_REFRESH_AUTHENTICATION_TOKEN)
        .then()
        .expect(status().isOk());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void refreshAuthenticationTokenShouldAllowAccessForDoctor() {
    String requestBody = createRequestBodyForCreateAuthenticationToken(
        TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME,
        TestDataConstants.DOCTOR_WITH_PATIENTS_PASSWORD
    );

    String token = RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_AUTHENTICATION_TOKEN)
        .then()
        .body("$", hasKey("token"))
        .extract()
        .path("token");

    RestAssuredMockMvc.given().mockMvc(mvc)
        .header(jwtHeader, JWT_HEADER_START + token)
        .when()
        .get(URI_FOR_REFRESH_AUTHENTICATION_TOKEN)
        .then()
        .expect(status().isOk());
  }

  @Test
  @WithUserDetails(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME)
  public void refreshAuthenticationTokenShouldAllowAccessForPatient() {
    String requestBody = createRequestBodyForCreateAuthenticationToken(
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_PASSWORD
    );

    String token = RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_AUTHENTICATION_TOKEN)
        .then()
        .body("$", hasKey("token"))
        .extract()
        .path("token");

    RestAssuredMockMvc.given().mockMvc(mvc)
        .header(jwtHeader, JWT_HEADER_START + token)
        .when()
        .get(URI_FOR_REFRESH_AUTHENTICATION_TOKEN)
        .then()
        .expect(status().isOk());
  }

  @Test
  @WithUserDetails(TestDataConstants.PHARMACIST_USERNAME)
  public void refreshAuthenticationTokenShouldAllowAccessForPharmacist() {
    String requestBody = createRequestBodyForCreateAuthenticationToken(
        TestDataConstants.PHARMACIST_USERNAME,
        TestDataConstants.PHARMACIST_PASSWORD
    );

    String token = RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_AUTHENTICATION_TOKEN)
        .then()
        .body("$", hasKey("token"))
        .extract()
        .path("token");

    RestAssuredMockMvc.given().mockMvc(mvc)
        .header(jwtHeader, JWT_HEADER_START + token)
        .when()
        .get(URI_FOR_REFRESH_AUTHENTICATION_TOKEN)
        .then()
        .expect(status().isOk());
  }

  @Test
  @WithUserDetails(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME)
  public void refreshAuthenticationTokenShouldReturnNewValidToken() {
    String requestBody = createRequestBodyForCreateAuthenticationToken(
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME,
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_PASSWORD
    );

    String oldToken = RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CREATE_AUTHENTICATION_TOKEN)
        .then()
        .body("$", hasKey("token"))
        .extract()
        .path("token");

    String newToken = RestAssuredMockMvc.given().mockMvc(mvc)
        .header(jwtHeader, JWT_HEADER_START + oldToken)
        .when()
        .get(URI_FOR_REFRESH_AUTHENTICATION_TOKEN)
        .then()
        .body("$", hasKey("token"))
        .extract()
        .path("token");

    RestAssuredMockMvc.given().mockMvc(mvc)
        .header(jwtHeader, JWT_HEADER_START + newToken)
        .when()
        .get(URI_FOR_REFRESH_AUTHENTICATION_TOKEN)
        .then()
        .expect(status().isOk());
  }

  @Test
  public void changePasswordShouldDenyAccessForUnauthenticatedUser() {
    String requestBody = createRequestBodyForChangePassword(
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_PASSWORD,
        "newPassword"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CHANGE_PASSWORD)
        .then()
        .expect(status().isUnauthorized());
  }

  @Test
  @WithUserDetails(TestDataConstants.ADMINISTRATOR_USERNAME)
  public void changePasswordShouldAllowAccessForAdministrator() {
    String requestBody = createRequestBodyForChangePassword(
        TestDataConstants.ADMINISTRATOR_PASSWORD,
        "newPassword"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CHANGE_PASSWORD)
        .then()
        .expect(status().isOk());
  }

  @Test
  @WithUserDetails(TestDataConstants.DOCTOR_WITH_PATIENTS_USERNAME)
  public void changePasswordShouldAllowAccessForDoctor() {
    String requestBody = createRequestBodyForChangePassword(
        TestDataConstants.DOCTOR_WITH_PATIENTS_PASSWORD,
        "newPassword"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CHANGE_PASSWORD)
        .then()
        .expect(status().isOk());
  }

  @Test
  @WithUserDetails(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME)
  public void changePasswordShouldAllowAccessForPatient() {
    String requestBody = createRequestBodyForChangePassword(
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_PASSWORD,
        "newPassword"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CHANGE_PASSWORD)
        .then()
        .expect(status().isOk());
  }

  @Test
  @WithUserDetails(TestDataConstants.PHARMACIST_USERNAME)
  public void changePasswordShouldAllowAccessForPharmacist() {
    String requestBody = createRequestBodyForChangePassword(
        TestDataConstants.PHARMACIST_PASSWORD,
        "newPassword"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CHANGE_PASSWORD)
        .then()
        .expect(status().isOk());
  }

  @Test
  @WithUserDetails(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME)
  public void changePasswordShouldDenyAccessWhenIncorrectOldPasswordIsProvided() {
    String requestBody = createRequestBodyForChangePassword(
        "incorrectOldPassword",
        "newPassword"
    );

    RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CHANGE_PASSWORD)
        .then()
        .expect(status().isUnauthorized());
  }

  @Test
  @WithUserDetails(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME)
  public void changePasswordShouldReturnNewValidToken() {
    String requestBody = createRequestBodyForChangePassword(
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_PASSWORD,
        "newPassword"
    );

    String newToken = RestAssuredMockMvc.given().mockMvc(mvc)
        .contentType(ContentType.JSON)
        .body(requestBody)
        .when()
        .post(URI_FOR_CHANGE_PASSWORD)
        .then()
        .body("$", hasKey("token"))
        .extract()
        .path("token");

    RestAssuredMockMvc.given().mockMvc(mvc)
        .header(jwtHeader, JWT_HEADER_START + newToken)
        .when()
        .get(URI_FOR_REFRESH_AUTHENTICATION_TOKEN)
        .then()
        .expect(status().isOk());
  }
}
