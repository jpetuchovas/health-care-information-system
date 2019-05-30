package com.justinaspetuchovas.health.service;

import com.justinaspetuchovas.health.common.TimeProvider;
import com.justinaspetuchovas.health.exception.MedicalPrescriptionDoesNotBelongToPatientException;
import com.justinaspetuchovas.health.exception.MedicalPrescriptionNotFoundException;
import com.justinaspetuchovas.health.exception.MedicalPrescriptionValidityEndedException;
import com.justinaspetuchovas.health.exception.UserNotFoundException;
import com.justinaspetuchovas.health.model.prescription.MedicalPrescription;
import com.justinaspetuchovas.health.model.prescription.MedicalPrescriptionDto;
import com.justinaspetuchovas.health.model.prescription.MedicalPrescriptionWithPurchaseFactCountDto;
import com.justinaspetuchovas.health.model.prescription.MedicalPrescriptionProjection;
import com.justinaspetuchovas.health.model.prescription.MedicalPrescriptionProjectionWithPurchaseFactCount;
import com.justinaspetuchovas.health.model.prescription.NewMedicalPrescriptionCommand;
import com.justinaspetuchovas.health.model.prescription.PurchaseFact;
import com.justinaspetuchovas.health.model.user.doctor.Doctor;
import com.justinaspetuchovas.health.model.user.patient.Patient;
import com.justinaspetuchovas.health.model.user.pharmacist.Pharmacist;
import com.justinaspetuchovas.health.pagination.PageCommand;
import com.justinaspetuchovas.health.pagination.SliceDto;
import com.justinaspetuchovas.health.repository.DoctorRepository;
import com.justinaspetuchovas.health.repository.MedicalPrescriptionRepository;
import com.justinaspetuchovas.health.repository.PatientRepository;
import com.justinaspetuchovas.health.repository.PharmacistRepository;
import com.justinaspetuchovas.health.repository.PurchaseFactRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service used to perform operations with medical prescriptions.
 */
@Service
public class MedicalPrescriptionService {
  private static final Logger logger = LogManager.getLogger(MedicalPrescriptionService.class);
  private final MedicalPrescriptionRepository medicalPrescriptionRepository;
  private final PurchaseFactRepository purchaseFactRepository;
  private final PatientRepository patientRepository;
  private final DoctorRepository doctorRepository;
  private final PharmacistRepository pharmacistRepository;
  private final TimeProvider timeProvider;

  @Autowired
  public MedicalPrescriptionService(
      MedicalPrescriptionRepository medicalPrescriptionRepository,
      PurchaseFactRepository purchaseFactRepository,
      PatientRepository patientRepository,
      DoctorRepository doctorRepository,
      PharmacistRepository pharmacistRepository,
      TimeProvider timeProvider
  ) {
    this.medicalPrescriptionRepository = medicalPrescriptionRepository;
    this.purchaseFactRepository = purchaseFactRepository;
    this.patientRepository = patientRepository;
    this.doctorRepository = doctorRepository;
    this.pharmacistRepository = pharmacistRepository;
    this.timeProvider = timeProvider;
  }

  private Date getYesterdaysDate(Date currentDate) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(currentDate);
    calendar.add(Calendar.DAY_OF_YEAR, -1);
    return calendar.getTime();
  }

  @Transactional
  public void createMedicalPrescription(
      UUID patientId,
      UUID doctorId,
      NewMedicalPrescriptionCommand newMedicalPrescriptionCommand
  ) {
    Doctor doctor = doctorRepository.findOne(doctorId);
    Patient patient = patientRepository.findOne(patientId);

    if (doctor == null) {
      logger.warn(
          "Could not create a medical prescription because a doctor with id \"{}\" does not exist.",
          doctorId
      );
      throw new UserNotFoundException("Doctor does not exist.");
    } else if (patient == null) {
      logger.warn(
          "Could not create a medical prescription because a patient with "
              + "id \"{}\" does not exist.",
          patientId
      );
      throw new UserNotFoundException("Patient does not exist.");
    } else {
      String activeIngredient = newMedicalPrescriptionCommand.getActiveIngredient();
      MedicalPrescription medicalPrescription = new MedicalPrescription(
          activeIngredient,
          newMedicalPrescriptionCommand.getActiveIngredientQuantity(),
          newMedicalPrescriptionCommand.getActiveIngredientMeasurementUnit(),
          newMedicalPrescriptionCommand.getUsageDescription(),
          timeProvider.now(),
          newMedicalPrescriptionCommand.getHasUnlimitedValidity(),
          newMedicalPrescriptionCommand.getValidityEndDate(),
          doctor,
          patient
      );

      medicalPrescriptionRepository.save(medicalPrescription);
      logger.info(
          "Created a medical prescription (active ingredient: \"{}\") for the user with "
              + "the username \"{}\".",
          activeIngredient,
          patient.getUsername()
      );
    }
  }

  @Transactional(readOnly = true)
  public SliceDto getMedicalPrescriptions(UUID patientId, PageCommand pageCommand) {
    int pageNumber = pageCommand.getPageNumber();
    int pageSize = pageCommand.getPageSize();

    Slice<MedicalPrescriptionProjectionWithPurchaseFactCount> medicalPrescriptionsSlice =
        medicalPrescriptionRepository.getMedicalPrescriptions(
            patientId,
            new PageRequest(
                pageNumber,
                pageSize,
                Sort.Direction.DESC,
                "hasUnlimitedValidity",
                "validityEndDate",
                "id"
            )
        );

    Date yesterdaysDate = getYesterdaysDate(timeProvider.now());

    logger.info(
        "Returned a page number {} of size {} with a list of the patient's with id \"{}\" medical "
            + "prescriptions.",
        pageNumber,
        pageSize,
        patientId
    );

    // MedicalPrescriptionWithPurchaseFactCountDto is used here instead of returning
    // MedicalPrescriptionProjectionWithPurchaseFactCount because it is not possible to pass
    // parameters to the JPA query's SELECT clause if they are not used in the WHERE clause.
    // TimeProvider is used in order to be able to test the method by setting any date
    // and not depending on the current date.
    return new SliceDto(
        medicalPrescriptionsSlice.getContent()
            .stream()
            .map(medicalPrescription -> new MedicalPrescriptionWithPurchaseFactCountDto(
                medicalPrescription.getId(),
                medicalPrescription.getActiveIngredient(),
                medicalPrescription.getActiveIngredientQuantity(),
                medicalPrescription.getActiveIngredientMeasurementUnit(),
                medicalPrescription.getUsageDescription(),
                medicalPrescription.getIssueDate(),
                medicalPrescription.getHasUnlimitedValidity(),
                medicalPrescription.getValidityEndDate(),
                medicalPrescription.getDoctorFullName(),
                medicalPrescription.getPurchaseFactCount(),
                medicalPrescription.getHasUnlimitedValidity()
                    || medicalPrescription.getValidityEndDate().after(yesterdaysDate)
            ))
            .collect(Collectors.toList()),
        medicalPrescriptionsSlice.hasNext()
    );
  }

  @Transactional(readOnly = true)
  public SliceDto getValidMedicalPrescriptions(UUID patientId, PageCommand pageCommand) {
    int pageNumber = pageCommand.getPageNumber();
    int pageSize = pageCommand.getPageSize();

    Slice<MedicalPrescriptionProjection> validMedicalPrescriptionsSlice =
        medicalPrescriptionRepository.getValidMedicalPrescriptions(
            patientId,
            timeProvider.now(),
            new PageRequest(
                pageNumber,
                pageSize,
                Sort.Direction.DESC,
                "hasUnlimitedValidity",
                "validityEndDate",
                "id"
            )
        );

    logger.info(
        "Returned a page number {} of size {} with a list of the patient's with "
            + "id \"{}\" valid medical prescriptions.",
        pageNumber,
        pageSize,
        patientId
    );

    // MedicalPrescriptionDto is used here instead of returning MedicalPrescriptionProjection
    // in order to convert some activeIngredientMeasurementUnit values to lowercase.
    return new SliceDto(
        validMedicalPrescriptionsSlice.getContent()
            .stream()
            .map(medicalPrescription -> new MedicalPrescriptionDto(
                medicalPrescription.getId(),
                medicalPrescription.getActiveIngredient(),
                medicalPrescription.getActiveIngredientQuantity(),
                medicalPrescription.getActiveIngredientMeasurementUnit(),
                medicalPrescription.getUsageDescription(),
                medicalPrescription.getIssueDate(),
                medicalPrescription.getHasUnlimitedValidity(),
                medicalPrescription.getValidityEndDate(),
                medicalPrescription.getDoctorFullName()
            ))
            .collect(Collectors.toList()),
        validMedicalPrescriptionsSlice.hasNext()
    );
  }

  @Transactional
  public void markMedicalPrescriptionsAsUsed(
      UUID patientId,
      UUID pharmacistId,
      Set<UUID> medicalPrescriptionIds
  ) {
    Pharmacist pharmacist = pharmacistRepository.findOne(pharmacistId);
    Date currentDate = timeProvider.now();
    Date yesterdaysDate = getYesterdaysDate(currentDate);

    medicalPrescriptionIds
        .forEach(medicalPrescriptionId -> {
          MedicalPrescription medicalPrescription = medicalPrescriptionRepository.findOne(
              medicalPrescriptionId
          );

          if (medicalPrescription == null) {
            logger.warn(
                "Could not mark a medical prescription as used because a medical prescription "
                    + "with id \"{}\" does not exist.",
                medicalPrescriptionId
            );
            throw new MedicalPrescriptionNotFoundException();
          } else if (!medicalPrescription.getPatient().getId().equals(patientId)) {
            logger.warn(
                "Could not mark the medical prescription with id \"{}\" as used because "
                    + "it does not belong to the patient with id \"{}\".",
                medicalPrescriptionId,
                patientId
            );
            throw new MedicalPrescriptionDoesNotBelongToPatientException();
          } else if (
              !(medicalPrescription.getHasUnlimitedValidity()
                  || medicalPrescription.getValidityEndDate().after(yesterdaysDate))
          ) {
            logger.warn(
                "Could not mark the medical prescription with id \"{}\" as used because "
                    + "the validity of the medical prescription has ended.",
                medicalPrescriptionId
            );
            throw new MedicalPrescriptionValidityEndedException();
          } else {
            PurchaseFact purchaseFact = new PurchaseFact(
                pharmacist,
                medicalPrescription,
                currentDate
            );

            purchaseFactRepository.save(purchaseFact);
          }
        });

    logger.info(
        "Marked medical prescriptions with ids {} as used.",
        Arrays.toString(medicalPrescriptionIds.toArray())
    );
  }

  @Transactional(readOnly = true)
  public boolean doesMedicalPrescriptionBelongToPatient(
      UUID medicalPrescriptionId,
      UUID patientId
  ) {
    return medicalPrescriptionRepository.doesMedicalPrescriptionBelongToPatient(
        medicalPrescriptionId,
        patientId
    );
  }
}
