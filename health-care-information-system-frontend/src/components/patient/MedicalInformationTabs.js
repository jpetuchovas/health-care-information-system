import { state as globalState } from 'lape';
import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { Tab, Tabs } from 'react-bootstrap';

import MedicalPrescriptionListContainer from '../common/medical-information/MedicalPrescriptionListContainer';
import MedicalRecordListContainer from '../common/medical-information/MedicalRecordListContainer';

const PAGE_SIZE = 10;

class MedicalInformationTabs extends Component {
  static contextTypes = {
    router: PropTypes.object.isRequired,
  };

  state = {
    initialTab:
      this.props.location.pathname === '/medical-information/medical-records'
        ? 1
        : 2,
  };

  handleSelect = key => {
    this.context.router.replace(
      `/medical-information/${
        key === 1 ? 'medical-records' : 'medical-prescriptions'
      }`
    );
  };

  render() {
    return (
      <Tabs
        defaultActiveKey={this.state.initialTab}
        id="medical-information-tabs"
        onSelect={this.handleSelect}
        justified
      >
        <Tab eventKey={1} title="Ligos istorija">
          <MedicalRecordListContainer
            patientId={globalState.userId}
            pageSize={PAGE_SIZE}
            noMedicalRecordsNotificationTitle="Jūs dar neturite ligos istorijos įrašų."
            noMedicalRecordsNotificationQuestion="Jūsų ligos istorija neturėtų būti tuščia?"
          />
        </Tab>

        <Tab eventKey={2} title="Išrašyti receptai">
          <MedicalPrescriptionListContainer
            patientId={globalState.userId}
            pageSize={PAGE_SIZE}
            noMedicalPrescriptionsNotificationTitle="Dar neturite Jums išrašytų receptų."
            noMedicalPrescriptionsNotificationQuestion="Jums išrašytų receptų sąrašas neturėtų būti tuščias?"
          />
        </Tab>
      </Tabs>
    );
  }
}

export default MedicalInformationTabs;
