import React from 'react';

import PharmacistNavigationComponent from './PharmacistNavigationComponent';
import RoleApp from '../RoleApp';

const PharmacistApp = props => (
  <RoleApp navigationComponent={<PharmacistNavigationComponent />}>
    {props.children}
  </RoleApp>
);

export default PharmacistApp;
