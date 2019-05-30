package com.justinaspetuchovas.health.controller;

import com.justinaspetuchovas.health.filter.DoctorFilter;
import com.justinaspetuchovas.health.model.user.doctor.NewDoctorCommand;
import com.justinaspetuchovas.health.pagination.SliceDto;
import com.justinaspetuchovas.health.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

/**
 * Controller that handles requests to perform operations with doctors.
 */
@RestController
@RequestMapping("/api/doctors")
public class DoctorController {
  private final DoctorService doctorService;

  @Autowired
  public DoctorController(DoctorService doctorService) {
    this.doctorService = doctorService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public void createDoctor(@Valid @RequestBody NewDoctorCommand newDoctorCommand) {
    doctorService.createDoctor(newDoctorCommand);
  }

  @PostMapping("/filter/admin")
  public SliceDto filterDoctors(@Valid @RequestBody DoctorFilter doctorFilter) {
    return doctorService.filterDoctors(doctorFilter);
  }

  @PutMapping("/{doctorId}/patients/{patientId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void assignPatient(@PathVariable UUID doctorId, @PathVariable UUID patientId) {
    doctorService.assignPatient(doctorId, patientId);
  }
}
