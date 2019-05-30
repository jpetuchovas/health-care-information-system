import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { Col, Image } from 'react-bootstrap';

import faceSad from '../../images/face-sad.svg';
import './NotFoundPage.css';

const styles = {
  image: {
    height: '90px',
  },

  mainContent: {
    alignItems: 'center',
    display: 'flex',
    minHeight: '100vh',
  },
};

class NotFoundPage extends Component {
  static contextTypes = {
    router: PropTypes.object.isRequired,
  };

  handleGoBackButtonClick = () => {
    this.context.router.goBack();
  };

  render() {
    return (
      <div style={styles.mainContent}>
        <Col sm={6} smOffset={3}>
          <div className="text-center">
            <Image src={faceSad} style={styles.image} />

            <h4>Puslapis nerastas</h4>
            <h5>
              Atsiprašome, Jūsų ieškomo puslapio nėra. Pasitikrinkite, ar
              teisingai įvedėte URL adresą.
            </h5>

            <span
              className="go-back-link"
              onClick={this.handleGoBackButtonClick}
            >
              Grįžti į prieš tai buvusį puslapį
            </span>
          </div>
        </Col>
      </div>
    );
  }
}

export default NotFoundPage;
