import React from 'react';
import { Nav, NavItem } from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';

const PharmacistNavigationComponent = () => (
  <Nav>
    <LinkContainer to="/purchase-fact-marking">
      <NavItem>Pirkimo faktų žymėjimas</NavItem>
    </LinkContainer>
  </Nav>
);

export default PharmacistNavigationComponent;
