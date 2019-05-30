import React from 'react';
import { Nav, NavItem } from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';

const PatientNavigationComponent = () => (
  <Nav>
    <LinkContainer to="/medical-information">
      <NavItem>Medicininė informacija</NavItem>
    </LinkContainer>
  </Nav>
);

export default PatientNavigationComponent;
