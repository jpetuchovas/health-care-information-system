import React from 'react';

import DoctorNavigationComponent from './DoctorNavigationComponent';
import RoleApp from '../RoleApp';

const DoctorApp = props => (
  <RoleApp navigationComponent={<DoctorNavigationComponent />}>
    {props.children}
  </RoleApp>
);

export default DoctorApp;
