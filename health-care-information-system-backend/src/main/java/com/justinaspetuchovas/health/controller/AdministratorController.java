package com.justinaspetuchovas.health.controller;

import com.justinaspetuchovas.health.model.user.administrator.NewAdministratorCommand;
import com.justinaspetuchovas.health.service.AdministratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Controller that handles requests to perform operations with administrators.
 */
@RestController
@RequestMapping("/api/administrators")
public class AdministratorController {
  private final AdministratorService administratorService;

  @Autowired
  public AdministratorController(AdministratorService administratorService) {
    this.administratorService = administratorService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public void createAdministrator(
      @Valid @RequestBody NewAdministratorCommand newAdministratorCommand
  ) {
    administratorService.createAdministrator(newAdministratorCommand);
  }
}
