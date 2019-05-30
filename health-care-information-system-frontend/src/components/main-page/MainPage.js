import React from 'react';
import { Col, Row } from 'react-bootstrap';

import MainPageCard from './MainPageCard';

const styles = {
  // This is used to vertically center the panel in the browser's viewport.
  row: {
    alignItems: 'center',
    display: 'flex',
    minHeight: '100vh',
    margin: 0,
  },
};

const MainPage = () => (
  <Row style={styles.row}>
    <Col xs={6} md={3} mdOffset={3}>
      <MainPageCard
        glyph="stats"
        title="Statistika"
        subtitle="Viešai prieinama statistika."
        linkTo="/public-statistics/diseases"
      />
    </Col>

    <Col xs={6} md={3}>
      <MainPageCard
        glyph="user"
        title="Prisijungimas"
        subtitle="Prisijungimas prie „Medika“."
        linkTo="/login"
      />
    </Col>
  </Row>
);

export default MainPage;
