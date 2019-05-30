// Adds thousands separators to a number.
export const addThousandsSeparators = number => number.toLocaleString('de');

// Transforms each medical prescription's active ingredient quantity to have thousands
// separators and three decimal places as well as include the measurement unit.
export const transformActiveIngredientQuantity = medicalPrescriptions =>
  medicalPrescriptions.map(medicalPrescription => {
    const {
      activeIngredientMeasurementUnit,
      ...medicalPrescriptionWithoutMeasurementUnit
    } = medicalPrescription;

    return {
      ...medicalPrescriptionWithoutMeasurementUnit,
      activeIngredientQuantity: `${addThousandsSeparators(
        parseFloat(
          medicalPrescriptionWithoutMeasurementUnit.activeIngredientQuantity.toFixed(
            3
          )
        )
      )} ${activeIngredientMeasurementUnit}`,
    };
  });

const MINUTES_IN_ONE_HOUR = 60;

// Converts minutes (if the duration is more than one hour) to a text representation including
// hours and minutes.
export const convertMinutesToTextWithHours = minutes =>
  Math.floor(minutes / MINUTES_IN_ONE_HOUR) > 0
    ? `${Math.floor(minutes / MINUTES_IN_ONE_HOUR)} val. ${minutes %
        MINUTES_IN_ONE_HOUR} min.`
    : `${minutes % MINUTES_IN_ONE_HOUR} min.`;

// Leaves only two decimal places for the percentage, uses dot as a decimal separator
// and adds the percent sign.
export const getPercentageText = percentage =>
  `${percentage.toFixed(2).replace('.', ',')} %`;
