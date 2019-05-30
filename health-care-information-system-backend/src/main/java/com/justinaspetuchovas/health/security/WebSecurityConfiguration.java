package com.justinaspetuchovas.health.security;

import com.justinaspetuchovas.health.model.user.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
  private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
  private UserDetailsService userDetailsService;
  private JwtUtil jwtUtil;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Autowired
  public void setRestAuthenticationEntryPoint(
      RestAuthenticationEntryPoint restAuthenticationEntryPoint
  ) {
    this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
  }

  @Autowired
  public void setUserDetailsService(UserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  @Autowired
  public void setJwtUtil(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  @Autowired
  public void configureAuthentication(
      AuthenticationManagerBuilder authenticationManagerBuilder,
      UserDetailsService userDetailsService
  ) throws Exception {
    authenticationManagerBuilder
        .userDetailsService(userDetailsService)
        .passwordEncoder(passwordEncoder());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    String administratorRole = SecurityConstants.ROLE_PREFIX + Role.ADMIN.name();
    String doctorRole = SecurityConstants.ROLE_PREFIX + Role.DOCTOR.name();
    String pharmacistRole = SecurityConstants.ROLE_PREFIX + Role.PHARMACIST.name();
    String patientRole = SecurityConstants.ROLE_PREFIX + Role.PATIENT.name();

    http.cors()
        .and()
        .csrf().disable()
        // H2 database console runs inside a frame, so we need to disable X-Frame-Options in
        // Spring Security for it to work.
        .headers().frameOptions().disable()
        .and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint)
        .and()
        .addFilterBefore(
            new JwtAuthenticationTokenFilter(userDetailsService, jwtUtil),
            UsernamePasswordAuthenticationFilter.class
        ).authorizeRequests()
        .antMatchers(
            HttpMethod.PUT,
            "/api/doctors/*/patients/*"
        ).hasAuthority(administratorRole)
        .antMatchers(
            "/api/administrators",
            "/api/doctors",
            "/api/doctors/filter/admin",
            "/api/patients",
            "/api/patients/filter/admin",
            "/api/pharmacists"
        ).hasAuthority(administratorRole)
        .antMatchers(
            HttpMethod.GET,
            "/api/doctors/*/patients/csv"
        ).hasAuthority(doctorRole)
        .antMatchers(
            HttpMethod.POST,
            "/api/patients/*/medical-prescriptions"
        ).hasAuthority(doctorRole)
        .antMatchers(
            "/api/doctors/*/patients",
            "/api/patients/*/medical-records",
            "/api/patients/*",
            "/doctors/*/statistics/visits/aggregate",
            "/doctors/*/statistics/visits/daily"
        ).hasAuthority(doctorRole)
        .antMatchers(
            HttpMethod.PUT,
            "/api/patients/*/medical-prescriptions"
        ).hasAuthority(pharmacistRole)
        .antMatchers(
            "/api/patients/*/medical-prescriptions/valid"
        ).hasAuthority(pharmacistRole)
        .antMatchers(
            "/api/patients/*/medical-records/page",
            "/api/patients/*/medical-prescriptions/page",
            "/api/patients/*/medical-prescriptions/*/purchase-facts"
        ).hasAnyAuthority(doctorRole, patientRole)
        .antMatchers(
            "/api/patients/filter/personal-identification-number"
        ).hasAnyAuthority(doctorRole, pharmacistRole)
        .antMatchers(HttpMethod.POST, "/api/login").permitAll()
        .antMatchers(HttpMethod.GET, "/api/public/**").permitAll()
        .anyRequest().authenticated();
  }

  @Override
  public void configure(WebSecurity web) {
    web.ignoring()
        .antMatchers(
            HttpMethod.GET,
            "/",
            "/static/media/*",
            "/static/favicon.ico",
            "/webjars/**",
            "/**/*.html",
            "/**/*.css",
            "/**/*.js"
        )
        // The following permissions are included to allow access to Swagger UI and H2 console.
        .antMatchers(
            "/v2/api-docs",
            "/configuration/ui",
            "/swagger-resources",
            "/configuration/security",
            "/swagger-resources/configuration/ui",
            "/swagger-resources/configuration/security",
            "/swagger-ui.html",
            "/console",
            "/console/**",
            "/favicon.ico"
      );
  }
}
