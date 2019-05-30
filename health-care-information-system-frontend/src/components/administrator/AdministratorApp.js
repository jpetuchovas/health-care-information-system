import React from 'react';

import AdministratorNavigationComponent from './AdministratorNavigationComponent';
import RoleApp from '../RoleApp';

const AdministratorApp = props => (
  <RoleApp navigationComponent={<AdministratorNavigationComponent />}>
    {props.children}
  </RoleApp>
);

export default AdministratorApp;
