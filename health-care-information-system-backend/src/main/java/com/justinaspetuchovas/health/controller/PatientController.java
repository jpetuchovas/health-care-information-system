package com.justinaspetuchovas.health.controller;

import com.justinaspetuchovas.health.exception.ForbiddenException;
import com.justinaspetuchovas.health.filter.PatientFilterForAdministrator;
import com.justinaspetuchovas.health.filter.PatientFilterForDoctor;
import com.justinaspetuchovas.health.filter.PersonalIdentificationNumberFilter;
import com.justinaspetuchovas.health.model.user.patient.NewPatientCommand;
import com.justinaspetuchovas.health.model.user.patient.PatientProjectionForCsvDownload;
import com.justinaspetuchovas.health.model.user.patient.PatientProjectionForDoctorOrPharmacist;
import com.justinaspetuchovas.health.pagination.SliceDto;
import com.justinaspetuchovas.health.security.JwtUser;
import com.justinaspetuchovas.health.service.PatientService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

/**
 * Controller that handles requests to perform operations with patients.
 */
@RestController
@RequestMapping("/api")
public class PatientController {
  private static final Logger logger = LogManager.getLogger(PatientController.class);
  private static final String CSV_FILE_NAME = "pacientai.csv";
  private static final String[] CSV_FILE_HEADER = {
      "Vardas",
      "Pavardė",
      "Asmens kodas",
      "Gimimo data"
  };
  private final PatientService patientService;

  @Autowired
  public PatientController(PatientService patientService) {
    this.patientService = patientService;
  }

  @PostMapping("/patients")
  @ResponseStatus(HttpStatus.CREATED)
  public void createPatient(@Valid @RequestBody NewPatientCommand newPatientCommand) {
    patientService.createPatient(newPatientCommand);
  }

  @GetMapping("/patients/{patientId}")
  public PatientProjectionForDoctorOrPharmacist getPatient(
      @PathVariable UUID patientId,
      Authentication authentication
  ) {
    UUID doctorId = ((JwtUser) authentication.getPrincipal()).getId();

    if (!patientService.isPatientAssignedToDoctor(patientId, doctorId)) {
      logger.warn(
          "Denied access because the doctor tried to access information about "
              + "the patient with id \"{}\" that is not assigned to him.",
          patientId
      );
      throw new ForbiddenException();
    } else {
      return patientService.getPatient(patientId);
    }
  }

  @PostMapping("/patients/filter/personal-identification-number")
  public PatientProjectionForDoctorOrPharmacist getPatient(
      @RequestBody PersonalIdentificationNumberFilter personalIdentificationNumberFilter
  ) {
    return patientService.getPatient(personalIdentificationNumberFilter);
  }

  @PostMapping("/patients/filter/admin")
  public SliceDto filterPatientsForAdministrator(
      @Valid @RequestBody PatientFilterForAdministrator patientFilterForAdministrator
  ) {
    return patientService.filterPatientsForAdministrator(patientFilterForAdministrator);
  }

  @PostMapping("/doctors/{doctorId}/patients")
  public SliceDto filterPatientsForDoctor(
      @PathVariable UUID doctorId,
      @Valid @RequestBody PatientFilterForDoctor patientFilterForDoctor,
      Authentication authentication
  ) {
    UUID userId = ((JwtUser) authentication.getPrincipal()).getId();

    if (!userId.equals(doctorId)) {
      logger.warn(
          "Denied access because the doctor with id \"{}\" tried to filter another doctor's "
              + "with id \"{}\" patient list.",
          userId,
          doctorId
      );
      throw new ForbiddenException();
    } else {
      return patientService.filterPatientsForDoctor(doctorId, patientFilterForDoctor);
    }
  }

  @GetMapping("/doctors/{doctorId}/patients/csv")
  public void downloadPatientCsv(
      @PathVariable UUID doctorId,
      HttpServletResponse response,
      Authentication authentication
  ) {
    UUID userId = ((JwtUser) authentication.getPrincipal()).getId();

    if (!userId.equals(doctorId)) {
      logger.warn(
          "Denied access because the doctor with id \"{}\" tried to download another doctor's "
              + "with id \"{}\" patient list in CSV format.",
          userId,
          doctorId
      );
      throw new ForbiddenException();
    } else {
      response.setContentType("text/csv; charset=UTF-8");
      response.setHeader(
          "Content-Disposition",
          MessageFormat.format("attachment; filename=\"{0}\"", CSV_FILE_NAME)
      );

      try {
        PrintWriter responseWriter = response.getWriter();
        // This character (called byte order mark) is necessary for Microsoft Excel
        // to correctly show files with UTF-8 encoding.
        responseWriter.write("\ufeff");

        ICsvBeanWriter csvWriter = new CsvBeanWriter(
            responseWriter,
            CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE
        );

        csvWriter.writeHeader(CSV_FILE_HEADER);
        List<PatientProjectionForCsvDownload> patients =
            patientService.getDoctorsPatients(doctorId);

        for (PatientProjectionForCsvDownload patient : patients) {
          csvWriter.write(
              patient,
              "firstName",
              "lastName",
              "personalIdentificationNumber",
              "birthDate"
          );
        }

        csvWriter.close();
        logger.info("Returned the doctor's patient list in CSV format.");
      } catch (IOException exception) {
        String message = "Could not produce a CSV file with the doctor's patient list.";
        logger.warn(message);
        throw new RuntimeException(message);
      }
    }
  }
}
