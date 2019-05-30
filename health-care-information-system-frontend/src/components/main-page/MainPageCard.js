import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { Glyphicon } from 'react-bootstrap';

import { TRANSPARENT_BLUE_COLOR } from '../../common/constants';
import './MainPageCard.css';

const styles = {
  glyphicon: {
    color: TRANSPARENT_BLUE_COLOR,
    fontSize: '12em',
    marginTop: '10px',
  },
};

class MainPageCard extends Component {
  static contextTypes = {
    router: PropTypes.object.isRequired,
  };

  static propTypes = {
    glyph: PropTypes.string.isRequired,
    title: PropTypes.string.isRequired,
    subtitle: PropTypes.string.isRequired,
    linkTo: PropTypes.string.isRequired,
  };

  render() {
    return (
      <div
        className="panel panel-default grow"
        onClick={() => this.context.router.push(this.props.linkTo)}
      >
        <div className="panel-body text-center">
          <Glyphicon glyph={this.props.glyph} style={styles.glyphicon} />
          <h4>{this.props.title}</h4>
          <p>{this.props.subtitle}</p>
        </div>
      </div>
    );
  }
}

export default MainPageCard;
