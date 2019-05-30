import React from 'react';
import { Nav, NavItem } from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';

const DoctorNavigationComponent = () => (
  <Nav>
    <LinkContainer to="/patients">
      <NavItem>Pacientų sąrašas</NavItem>
    </LinkContainer>

    <LinkContainer to="/medical-record">
      <NavItem>Naujas ligos įrašas</NavItem>
    </LinkContainer>

    <LinkContainer to="/medical-prescription">
      <NavItem>Naujas receptas</NavItem>
    </LinkContainer>

    <LinkContainer to="/visit-statistics">
      <NavItem>Darbo dienų statistika</NavItem>
    </LinkContainer>
  </Nav>
);

export default DoctorNavigationComponent;
