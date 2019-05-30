import axios from 'axios';
import PropTypes from 'prop-types';
import React, { Component } from 'react';
import Yup from 'yup';

import {
  ADMINISTRATOR_EMAIL,
  ADMINISTRATOR_PHONE_NUMBER,
  DESCRIPTION_LENGTH_MAX,
  PERSONAL_IDENTIFICATION_NUMBER_LENGTH,
  VISIT_DURATION_IN_HOURS_MAX,
} from '../../../common/constants';
import Formik from '../../common/form/FormikWithHasChanged';
import {
  getAuthorizationConfig,
  getRequestUrl,
} from '../../../common/requestUtils';
import {
  DISEASE_CODE_PATTERN,
  PERSONAL_IDENTIFICATION_NUMBER_PATTERN,
  PERSONAL_IDENTIFICATION_NUMBER_START_PATTERN,
  POSITIVE_INTEGERS_PATTERN,
} from '../../../common/validation';
import MedicalRecordCreationFormComponent from './MedicalRecordCreationFormComponent';

const MINUTES_IN_ONE_HOUR = 60;

const validationSchema = Yup.object().shape({
  personalIdentificationNumber: Yup.string()
    .required('Įveskite paciento asmens kodą.')
    .matches(
      PERSONAL_IDENTIFICATION_NUMBER_PATTERN,
      'Asmens kodas turi būti sudarytas tik iš skaitmenų.'
    )
    .matches(
      PERSONAL_IDENTIFICATION_NUMBER_START_PATTERN,
      'Asmens kodas turi prasidėti skaitmeniu 3, 4, 5 arba 6.'
    )
    .length(
      PERSONAL_IDENTIFICATION_NUMBER_LENGTH,
      `Asmens kodas turi būti sudarytas iš ${PERSONAL_IDENTIFICATION_NUMBER_LENGTH} skaitmenų.`
    ),

  isVisitCompensated: Yup.boolean(),
  isVisitRepeated: Yup.boolean(),
  diseaseCode: Yup.string()
    .required('Įveskite ligos kodą.')
    .matches(DISEASE_CODE_PATTERN, 'Įvestas ligos kodas nėra validus.'),

  visitDurationInMinutes: Yup.string()
    .test(
      'isLessThanMax',
      `Maksimali vizito trukmė yra ${VISIT_DURATION_IN_HOURS_MAX} val.`,
      value =>
        value == null ||
        !POSITIVE_INTEGERS_PATTERN.test(value) ||
        parseInt(value, 10) <= VISIT_DURATION_IN_HOURS_MAX * MINUTES_IN_ONE_HOUR
    )
    .matches(
      POSITIVE_INTEGERS_PATTERN,
      'Vizito trukmmė turi būti teigiamas sveikasis skaičius.'
    )
    .required('Įveskite vizito trukmę minutėmis.'),

  description: Yup.string()
    .required('Įveskite vizito aprašymą.')
    .max(
      DESCRIPTION_LENGTH_MAX,
      `Aprašymas negali būti ilgesnis nei ${DESCRIPTION_LENGTH_MAX} simbolių.`
    ),
});

class MedicalRecordCreationFormContainer extends Component {
  static contextTypes = {
    router: PropTypes.object.isRequired,
  };

  handleGoBackButtonClick = () => {
    this.context.router.goBack();
  };

  render() {
    return (
      <Formik
        enableReinitialize
        initialValues={{
          personalIdentificationNumber:
            (this.props.location.state &&
              this.props.location.state.personalIdentificationNumber) ||
            '',
          isVisitCompensated: false,
          isVisitRepeated: false,
          diseaseCode: '',
          visitDurationInMinutes: '',
          description: '',
        }}
        onSubmit={(values, { setSubmitting, setStatus, resetForm }) => {
          axios
            .post(
              getRequestUrl(
                '/api/patients/filter/personal-identification-number'
              ),
              {
                personalIdentificationNumber:
                  values.personalIdentificationNumber,
              },
              getAuthorizationConfig()
            )
            .then(response => {
              const patientId = response.data.id;

              if (patientId) {
                axios
                  .post(
                    getRequestUrl(`/api/patients/${patientId}/medical-records`),
                    {
                      isVisitCompensated: values.isVisitCompensated,
                      isVisitRepeated: values.isVisitRepeated,
                      diseaseCode: values.diseaseCode,
                      visitDurationInMinutes: values.visitDurationInMinutes,
                      description: values.description,
                    },
                    getAuthorizationConfig()
                  )
                  .then(() => {
                    resetForm();
                    setStatus({
                      serverValidationState: 'success',
                      serverError: {},
                      isAlertVisible: true,
                    });
                  })
                  .catch(() => {
                    setSubmitting(false);
                    setStatus({
                      serverValidationState: 'unknownError',
                      serverError: {},
                      isAlertVisible: true,
                    });
                  });
              } else {
                setSubmitting(false);
                setStatus({
                  serverValidationState: 'knownError',
                  serverError: {
                    personalIdentificationNumber: `Paciento su tokiu asmens kodu nebuvo rasta.
                      Jeigu manote, kad pacientas su tokiu asmens kodu turėtų egzistuoti „Medika“
                      sistemoje, susisiekite su administratoriumi paštu ${ADMINISTRATOR_EMAIL}
                      arba telefonu ${ADMINISTRATOR_PHONE_NUMBER}.`,
                    value: values.personalIdentificationNumber,
                  },
                  isAlertVisible: false,
                });
              }
            })
            .catch(() => {
              setSubmitting(false);
              setStatus({
                serverValidationState: 'unknownError',
                serverError: {},
                isAlertVisible: true,
              });
            });
        }}
        validationSchema={validationSchema}
        render={({
          hasChanged,
          errors,
          handleSubmit,
          isSubmitting,
          setStatus,
          status,
          touched,
          values,
        }) => (
          <MedicalRecordCreationFormComponent
            hasChanged={hasChanged}
            errors={errors}
            handleSubmit={handleSubmit}
            isSubmitting={isSubmitting}
            setStatus={setStatus}
            status={status}
            touched={touched}
            values={values}
            hasGoBackButton={!!this.props.location.state}
            handleGoBackButtonClick={this.handleGoBackButtonClick}
          />
        )}
      />
    );
  }
}

export default MedicalRecordCreationFormContainer;
