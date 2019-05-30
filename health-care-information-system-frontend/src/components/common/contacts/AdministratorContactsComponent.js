import React from 'react';
import {
  ADMINISTRATOR_EMAIL,
  ADMINISTRATOR_PHONE_NUMBER,
} from '../../../common/constants';

const AdministratorContactsComponent = () => (
  <p>
    <a href={`mailto:${ADMINISTRATOR_EMAIL}`}>Parašykite administratoriui</a>{' '}
    arba susisiekite telefonu <strong>{ADMINISTRATOR_PHONE_NUMBER}</strong>.
  </p>
);

export default AdministratorContactsComponent;
