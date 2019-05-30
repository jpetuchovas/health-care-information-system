import React from 'react';

import PatientNavigationComponent from './PatientNavigationComponent';
import RoleApp from '../RoleApp';

const PatientApp = props => (
  <RoleApp navigationComponent={<PatientNavigationComponent />}>
    {props.children}
  </RoleApp>
);

export default PatientApp;
