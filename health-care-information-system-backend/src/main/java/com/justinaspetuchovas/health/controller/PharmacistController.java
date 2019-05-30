package com.justinaspetuchovas.health.controller;

import com.justinaspetuchovas.health.model.user.pharmacist.NewPharmacistCommand;
import com.justinaspetuchovas.health.service.PharmacistService;
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
@RequestMapping("/api/pharmacists")
public class PharmacistController {
  private final PharmacistService pharmacistService;

  @Autowired
  public PharmacistController(PharmacistService pharmacistService) {
    this.pharmacistService = pharmacistService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public void createPharmacist(@Valid @RequestBody NewPharmacistCommand newPharmacistCommand) {
    pharmacistService.createPharmacist(newPharmacistCommand);
  }
}
