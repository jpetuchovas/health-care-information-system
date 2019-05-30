package com.justinaspetuchovas.health.model.prescription;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.text.MessageFormat;

/**
 * Converter that transforms {@link ActiveIngredientMeasurementUnit} to lowercase representation
 * when saving it to a database, and coverts it back to enum when reading it from a database.
 */
@Converter(autoApply = true)
public class ActiveIngredientMeasurementUnitConverter
    implements AttributeConverter<ActiveIngredientMeasurementUnit, String> {
  @Override
  public String convertToDatabaseColumn(
      ActiveIngredientMeasurementUnit activeIngredientMeasurementUnit
  ) {
    switch (activeIngredientMeasurementUnit) {
      case MG: {
        return "mg";
      }
      case MCG: {
        return "mcg";
      }
      case IU: {
        return "IU";
      }
      default: {
        throw new IllegalArgumentException(
            MessageFormat.format(
                "Unknown active ingredient measurement unit {0}.",
                activeIngredientMeasurementUnit
            )
        );
      }
    }
  }

  @Override
  public ActiveIngredientMeasurementUnit convertToEntityAttribute(
      String activeIngredientMeasurementUnitInDatabase
  ) {
    switch (activeIngredientMeasurementUnitInDatabase) {
      case "mg": {
        return ActiveIngredientMeasurementUnit.MG;
      }
      case "mcg": {
        return ActiveIngredientMeasurementUnit.MCG;
      }
      case "IU": {
        return ActiveIngredientMeasurementUnit.IU;
      }
      default: {
        throw new IllegalArgumentException(
            MessageFormat.format(
                "Unknown active ingredient measurement unit {0} in database.",
                activeIngredientMeasurementUnitInDatabase
            )
        );
      }
    }
  }
}
