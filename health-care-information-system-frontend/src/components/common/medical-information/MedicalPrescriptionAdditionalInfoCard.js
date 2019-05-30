import React, { Fragment } from 'react';
import { Well } from 'react-bootstrap';

import CloseButton from '../closing/CloseButton';
import PurchaseFactListContainer from './PurchaseFactListContainer';

const styles = {
  medicalPrescriptionCardTextWithLoweredOpacity: {
    opacity: 0.7,
  },

  panel: {
    marginLeft: '15px',
  },

  panelBody: {
    maxHeight: '400px',
    minHeight: '400px',
    overflowY: 'auto',
  },

  spacedElements: {
    display: 'flex',
    justifyContent: 'space-between',
  },

  table: {
    marginBottom: '5px',
  },
};

const getPurchaseFactCountText = purchaseFactCount => {
  if (
    (purchaseFactCount % 100 >= 11 && purchaseFactCount % 100 <= 19) ||
    purchaseFactCount % 10 === 0
  ) {
    return 'kartų';
  } else if (purchaseFactCount % 10 === 1) {
    return 'kartą';
  } else {
    return 'kartus';
  }
};

const MedicalPrescriptionAdditionalInfoCard = ({
  selectedMedicalPrescription,
  handleCloseButtonClick,
  patientId,
}) => (
  <div className="panel panel-default" style={styles.panel}>
    <div className="panel-body" style={styles.panelBody}>
      <div style={styles.spacedElements}>
        <CloseButton handleClose={handleCloseButtonClick} />
        <span>
          {selectedMedicalPrescription.hasUnlimitedValidity
            ? 'Galiojimo trukmė neterminuota'
            : `${
                selectedMedicalPrescription.isValid ||
                selectedMedicalPrescription.isValid === undefined
                  ? 'Galioja iki'
                  : 'Galiojimas baigėsi'
              } ${selectedMedicalPrescription.validityEndDate}`}
        </span>
      </div>

      <div style={styles.spacedElements}>
        <h4 className="text-primary">
          {selectedMedicalPrescription.activeIngredient}
        </h4>
        <h4 className="text-muted">
          Gydytojas {selectedMedicalPrescription.doctorFullName}
        </h4>
      </div>

      <div
        style={{
          ...styles.spacedElements,
          ...styles.medicalPrescriptionCardTextWithLoweredOpacity,
        }}
      >
        <p>
          {selectedMedicalPrescription.activeIngredientQuantity} vienoje dozėje
        </p>
        <p>Receptas išrašytas {selectedMedicalPrescription.issueDate}</p>
      </div>

      <h5>Vartojimo aprašymas</h5>
      <p className="text-justify">
        {selectedMedicalPrescription.usageDescription}
      </p>

      {patientId ? (
        <Fragment>
          <Well bsSize="small">
            Receptas panaudotas{' '}
            {`${
              selectedMedicalPrescription.purchaseFactCount
            } ${getPurchaseFactCountText(
              selectedMedicalPrescription.purchaseFactCount
            )}.`}
          </Well>

          {selectedMedicalPrescription.purchaseFactCount > 0 ? (
            <PurchaseFactListContainer
              patientId={patientId}
              medicalPrescriptionId={selectedMedicalPrescription.id}
            />
          ) : null}
        </Fragment>
      ) : null}
    </div>
  </div>
);

export default MedicalPrescriptionAdditionalInfoCard;
