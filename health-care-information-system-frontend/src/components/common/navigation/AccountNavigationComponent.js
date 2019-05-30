import { state as globalState } from 'lape';
import React from 'react';
import { MenuItem, Nav, NavDropdown } from 'react-bootstrap';
import { IndexLinkContainer, LinkContainer } from 'react-router-bootstrap';

import { logOut } from '../../../common/jwtUtils';

const AccountNavigationComponent = () => (
  <Nav pullRight>
    <NavDropdown title={globalState.name} id="nav-dropdown">
      <LinkContainer to="password-change">
        <MenuItem>Keisti slaptažodį</MenuItem>
      </LinkContainer>

      <MenuItem divider />

      <IndexLinkContainer to="/">
        <MenuItem onClick={logOut}>Atsijungti</MenuItem>
      </IndexLinkContainer>
    </NavDropdown>
  </Nav>
);

export default AccountNavigationComponent;
