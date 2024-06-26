import React from 'react';
import PropTypes from 'prop-types';
import clsx from 'clsx';
import { makeStyles } from '@material-ui/styles';
import { Grid } from '@material-ui/core';

const useStyles = makeStyles(() => ({
  root: {}
}));

/**
 * Summary information grid
 * 
 * @param {Object} component Component info to grid
 * 
 * @author Allan de Miranda
 */
const Summary = props => {
  const { className, component, ...rest } = props;

  const classes = useStyles();  

  return (
    <Grid
      {...rest}
      className={clsx(classes.root, className)}
      container
      spacing={3}
    >
      <Grid
        item
        lg={12}
        md={12}
        xl={12}
        xs={12}
      >
        {component}
      </Grid> 
    </Grid>
  );
};

Summary.propTypes = {
  /**
   * Class component
   */
  className: PropTypes.string,
  /**
   * Component to grid
   */
  component: PropTypes.object.isRequired
};

export default Summary;
