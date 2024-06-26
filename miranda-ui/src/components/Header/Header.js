import React from 'react';
import PropTypes from 'prop-types';
import clsx from 'clsx';
import { makeStyles } from '@material-ui/styles';
import { Grid, Typography } from '@material-ui/core';

const useStyles = makeStyles(() => ({
  root: {}
}));

/**
 * Page information header
 * 
 * @param {String} subTitle The header sub title
 * @param {String} title The header title
 * 
 * @author Allan de Miranda
 */
const Header = props => {
  const { className, subTitle, title, ...rest } = props;

  const classes = useStyles();

  return (
    <div
      {...rest}
      className={clsx(classes.root, className)}
    >
      <Grid
        alignItems="flex-end"
        container
        justify="space-between"
        spacing={3}
      >
        <Grid item>
          <Typography
            component="h2"
            gutterBottom
            variant="overline"
          >
            {subTitle}
          </Typography>
          <Typography
            component="h1"
            variant="h3"
          >
            {title}
          </Typography>
        </Grid>        
      </Grid>
    </div>
  );
};

Header.propTypes = {
  /**
   * Class component
   */
  className: PropTypes.string,
  /**
   * The header sub title
   */
  subTitle: PropTypes.string.isRequired,
  /**
   * The header title
   */
  title: PropTypes.string.isRequired  
};

export default Header;
