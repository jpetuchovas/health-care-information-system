package com.justinaspetuchovas.health.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public final class TestDataConstants {
  public static final String ADMINISTRATOR_ID = "9799dbc7-7922-417d-8b4a-4a46f65ee3c6";
  public static final String ADMINISTRATOR_USERNAME = "admin2";
  public static final String ADMINISTRATOR_PASSWORD = "doctor";
  public static final String ADMINISTRATOR_FIRST_NAME = "doctor";


  public static final String DOCTOR_WITH_PATIENTS_ID = "cf373bee-6547-43ea-a07e-62382c0d6fd5";
  public static final String DOCTOR_WITH_PATIENTS_USERNAME = "doctor";
  public static final String DOCTOR_WITH_PATIENTS_FIRST_NAME = "Dainius";
  public static final String DOCTOR_WITH_PATIENTS_LAST_NAME = "Dainaitis";
  public static final String DOCTOR_WITH_PATIENTS_SPECIALIZATION = "Odontologas";
  public static final String DOCTOR_WITH_PATIENTS_PASSWORD = "doctor";
  public static final String DOCTOR_WITHOUT_PATIENTS_ID = "a0c84931-f134-47c9-9bf7-57ae5fdb5f91";
  public static final String DOCTOR_WITHOUT_PATIENTS_USERNAME = "doctor2";

  public static final String PATIENT_WITH_MEDICAL_INFORMATION_ID =
      "29eb13a9-4c07-4e2e-b6bc-72a57a9868cd";
  public static final String PATIENT_WITH_MEDICAL_INFORMATION_USERNAME = "patient";
  public static final String PATIENT_WITH_MEDICAL_INFORMATION_FIRST_NAME = "Petras";
  public static final String PATIENT_WITH_MEDICAL_INFORMATION_LAST_NAME = "Petraitis";
  public static final String PATIENT_WITH_MEDICAL_INFORMATION_PERSONAL_ID_NUMBER = "51010101234";
  public static final String PATIENT_WITH_MEDICAL_INFORMATION_BIRTH_DATE = "2010-10-10";
  public static final String PATIENT_WITH_MEDICAL_INFORMATION_PASSWORD = "patient";
  public static final String PATIENT_WITHOUT_MEDICAL_INFORMATION_ID =
      "40120fa2-5d83-43b1-8e3f-7ddfb36d3dbb";
  public static final String PATIENT_WITHOUT_MEDICAL_INFORMATION_USERNAME = "patient2";

  public static final String PHARMACIST_ID = "9f58ad65-1b0c-4f0c-bbaa-1bfa7cd32140";
  public static final String PHARMACIST_USERNAME = "pharmacist";
  public static final String PHARMACIST_PASSWORD = "pharmacist";

  public static final String FIRST_MEDICAL_RECORD_ID = "5e800a4a-7c03-4115-831b-f39f49e075aa";
  public static final String SECOND_MEDICAL_RECORD_ID = "ad4300ec-1ff5-4ed8-9131-6dd142383d73";
  public static final String EARLIEST_MEDICAL_RECORD_DATE = "2017-12-01";
  public static final String LATEST_MEDICAL_RECORD_DATE = "2018-01-02";
  public static final String DATE_WITH_NO_MEDICAL_RECORDS = "2018-02-01";

  public static final String INVALID_MEDICAL_PRESCRIPTION_ID =
      "1e4c1574-ace1-44ce-a7ac-e5368be5afed";
  public static final String FIRST_VALID_MEDICAL_PRESCRIPTION_ID =
      "155e4bac-0abc-4ebd-b8cf-939ba3336c80";
  public static final String SECOND_VALID_MEDICAL_PRESCRIPTION_ID =
      "27ae9673-06b9-4a36-a95d-604f823aa179";
  public static final String ANOTHER_PATIENTS_MEDICAL_PRESCRIPTION_ID =
      "a542863d-594f-4a97-868f-c85215cf5ce8";

  public static final String FIRST_PURCHASE_FACT_PURCHASE_DATE = "2018-01-10";
  public static final String SECOND_PURCHASE_FACT_PURCHASE_DATE = "2018-01-12";

  public static final String NON_EXISTENT_ID = "a0c84931-f134-47c9-9bf7-57ae5fdb5f97";

  public static final Date CURRENT_TIME_FOR_TESTING =
      new GregorianCalendar(2018, Calendar.FEBRUARY, 1).getTime();

  private TestDataConstants() {
  }
}
