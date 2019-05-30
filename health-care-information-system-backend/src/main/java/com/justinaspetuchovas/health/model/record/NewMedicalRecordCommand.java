package com.justinaspetuchovas.health.model.record;

import com.justinaspetuchovas.health.common.ValidationConstants;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Request to create a new medical record.
 */
public class NewMedicalRecordCommand {
  @NotNull
  @Size(
      min = 1,
      max = ValidationConstants.DESCRIPTION_LENGTH_MAX,
      message = "Description must be between {min} and {max} characters long."
  )
  private String description;

  @NotNull
  @Min(ValidationConstants.VISIT_DURATION_IN_MINUTES_MIN)
  @Max(ValidationConstants.VISIT_DURATION_IN_MINUTES_MAX)
  @Digits(
      integer = ValidationConstants.VISIT_DURATION_IN_MINUTES_MAX_NUMBER_OF_DIGITS,
      fraction = 0
  )
  private short visitDurationInMinutes;

  @NotNull
  @Pattern(
      regexp = ValidationConstants.DISEASE_CODE_PATTERN,
      message = "The given value does not conform to the ICD-10 format."
  )
  private String diseaseCode;

  @NotNull
  private boolean isVisitCompensated;

  @NotNull
  private boolean isVisitRepeated;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public short getVisitDurationInMinutes() {
    return visitDurationInMinutes;
  }

  public void setVisitDurationInMinutes(short visitDurationInMinutes) {
    this.visitDurationInMinutes = visitDurationInMinutes;
  }

  public String getDiseaseCode() {
    return diseaseCode;
  }

  public void setDiseaseCode(String diseaseCode) {
    this.diseaseCode = diseaseCode;
  }

  public boolean getIsVisitCompensated() {
    return isVisitCompensated;
  }

  public void setIsVisitCompensated(boolean visitCompensated) {
    isVisitCompensated = visitCompensated;
  }

  public boolean getIsVisitRepeated() {
    return isVisitRepeated;
  }

  public void setIsVisitRepeated(boolean visitRepeated) {
    isVisitRepeated = visitRepeated;
  }
}
