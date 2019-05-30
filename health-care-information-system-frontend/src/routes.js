import React from 'react';
import { Route, IndexRoute, IndexRedirect } from 'react-router';

import { Role } from './common/constants';
import {
  rerouteIfLoggedIn,
  requireRole,
  requireToBeLoggedIn,
} from './common/routingUtils';
import App from './components/App';
import AdministratorApp from './components/administrator/AdministratorApp';
import PatientAssignmentContainer from './components/administrator/patient-assignment/PatientAssignmentContainer';
import RegistrationTabs from './components/administrator/registration/RegistrationTabs';
import DoctorApp from './components/doctor/DoctorApp';
import MedicalPrescriptionCreationFormContainer from './components/doctor/form/MedicalPrescriptionCreationFormContainer';
import MedicalRecordCreationFormContainer from './components/doctor/form/MedicalRecordCreationFormContainer';
import PatientCardContainer from './components/doctor/patient/PatientCardContainer';
import PatientListContainer from './components/doctor/patient/PatientListContainer';
import VisitStatisticsContainer from './components/doctor/visit-statistics/VisitStatisticsContainer';
import LoginPage from './components/login/LoginPage';
import MainPage from './components/main-page/MainPage';
import NotFoundPage from './components/not-found-page/NotFoundPage';
import PasswordChangePage from './components/password-change/PasswordChangePage';
import MedicalInformationTabs from './components/patient/MedicalInformationTabs';
import PatientApp from './components/patient/PatientApp';
import PharmacistApp from './components/pharmacist/PharmacistApp';
import SearchableMedicalPrescriptionListContainer from './components/pharmacist/SearchableMedicalPrescriptionListContainer';
import PublicStatisticsTabs from './components/public-statistics/PublicStatisticsTabs';

export default (
  <Route path="/" component={App}>
    <IndexRoute component={MainPage} />

    <Route path="public-statistics" component={PublicStatisticsTabs}>
      <IndexRedirect to="diseases" />
      <Route path="diseases" />
      <Route path="active-ingredients" />
    </Route>

    <Route onEnter={rerouteIfLoggedIn}>
      <Route path="login" component={LoginPage} />
    </Route>

    <Route component={AdministratorApp} onEnter={requireRole(Role.ADMIN)}>
      <Route path="registration" component={RegistrationTabs}>
        <IndexRedirect to="patient" />
        <Route path="patient" />
        <Route path="doctor" />
        <Route path="pharmacist" />
        <Route path="administrator" />
      </Route>

      <Route path="patient-assignment" component={PatientAssignmentContainer} />
    </Route>

    <Route component={DoctorApp} onEnter={requireRole(Role.DOCTOR)}>
      <Route path="patients" component={PatientListContainer} />
      <Route path="patients/:patientId" component={PatientCardContainer}>
        <IndexRedirect to="medical-records" />
        <Route path="medical-records" />
        <Route path="medical-prescriptions" />
      </Route>

      <Route
        path="medical-record"
        component={MedicalRecordCreationFormContainer}
      />
      <Route
        path="medical-prescription"
        component={MedicalPrescriptionCreationFormContainer}
      />
      <Route path="visit-statistics" component={VisitStatisticsContainer} />
    </Route>

    <Route component={PatientApp} onEnter={requireRole(Role.PATIENT)}>
      <Route path="medical-information" component={MedicalInformationTabs}>
        <IndexRedirect to="medical-records" />
        <Route path="medical-records" />
        <Route path="medical-prescriptions" />
      </Route>
    </Route>

    <Route component={PharmacistApp} onEnter={requireRole(Role.PHARMACIST)}>
      <Route
        path="purchase-fact-marking"
        component={SearchableMedicalPrescriptionListContainer}
      />
    </Route>

    <Route
      path="password-change"
      component={PasswordChangePage}
      onEnter={requireToBeLoggedIn}
    />

    <Route component={NotFoundPage}>
      <Route path="not-found" />
      <Route path="*" />
    </Route>
  </Route>
);
