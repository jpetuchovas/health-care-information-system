package com.justinaspetuchovas.health.security;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import com.justinaspetuchovas.health.controller.TestDataConstants;
import com.justinaspetuchovas.health.model.user.Role;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.UUID;

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
public class JwtAuthenticationTokenFilterIT {
  private static final String URI_FOR_REFRESH_AUTHENTICATION_TOKEN = "/api/refresh";
  private static final String JWT_HEADER_START = "Bearer ";
  private MockMvc mvc;

  @Value("${jwt.header}")
  private String jwtHeader;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private JwtUtil jwtUtil;

  @Before
  public void setup() {
    mvc = MockMvcBuilders
        .webAppContextSetup(webApplicationContext)
        .apply(springSecurity())
        .build();
  }

  @Test
  public void doFilterInternalShouldAllowValidToken() {
    JwtUser jwtUser =  new JwtUser(
        UUID.fromString(TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_ID),
        TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_USERNAME,
        "password",
        Collections.singletonList(
            new SimpleGrantedAuthority(SecurityConstants.ROLE_PREFIX + Role.PATIENT.name())
        ),
        TestDataConstants.CURRENT_TIME_FOR_TESTING,
        Role.PATIENT,
        MessageFormat.format(
            "{0} {1}",
            TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_FIRST_NAME,
            TestDataConstants.PATIENT_WITH_MEDICAL_INFORMATION_LAST_NAME
        )
    );

    String token = jwtUtil.generateToken(jwtUser);

    RestAssuredMockMvc.given().mockMvc(mvc)
        .header(jwtHeader, JWT_HEADER_START + token)
        .when()
        .get(URI_FOR_REFRESH_AUTHENTICATION_TOKEN)
        .then()
        .expect(status().isOk());
  }
}
