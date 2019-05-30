import React from 'react';
import { Grid } from 'react-bootstrap';

const styles = {
  grid: {
    padding: 0,
  },
};

const App = props => (
  <Grid fluid style={styles.grid}>
    {props.children}
  </Grid>
);

export default App;
