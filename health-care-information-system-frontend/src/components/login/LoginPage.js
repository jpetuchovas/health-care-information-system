import React from 'react';
import { Col } from 'react-bootstrap';

import LoginFormContainer from './LoginFormContainer';

const styles = {
  // This is used to vertically center the panel in the browser's viewport.
  loginPage: {
    alignItems: 'center',
    display: 'flex',
    minHeight: '100vh',
  },
};

const LoginPage = () => (
  <div style={styles.loginPage}>
    <Col sm={6} smOffset={3}>
      <div className="panel panel-default">
        <div className="panel-heading text-center">
          <div className="panel-title">Prisijungimas prie „Medika“</div>
        </div>

        <div className="panel-body">
          <LoginFormContainer />
        </div>
      </div>
    </Col>
  </div>
);

export default LoginPage;
