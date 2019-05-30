import React from 'react';
import { Nav, NavItem } from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';

const AdministratorNavigationComponent = () => (
  <Nav>
    <LinkContainer to="/registration">
      <NavItem>Vartotojo registracija</NavItem>
    </LinkContainer>

    <LinkContainer to="/patient-assignment">
      <NavItem>Paciento priskyrimas gydytojui</NavItem>
    </LinkContainer>
  </Nav>
);

export default AdministratorNavigationComponent;
