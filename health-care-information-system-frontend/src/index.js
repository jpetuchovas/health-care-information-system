import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap-daterangepicker/daterangepicker.css';
import { listen } from 'lape';
import React from 'react';
import ReactDOM from 'react-dom';
import { hashHistory, Router } from 'react-router';

import './globalState';
import routes from './routes';

const renderer = globalState => {
  ReactDOM.render(
    <Router history={hashHistory} routes={routes} />,
    document.getElementById('root')
  );
};

listen(renderer);
