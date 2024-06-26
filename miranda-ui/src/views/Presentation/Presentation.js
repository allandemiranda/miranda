/**
 * @description Welcome screen
 * 
 * @author Allan de Miranda
 */

import React from 'react';
import { makeStyles } from '@material-ui/styles';
import { Page } from 'components';
import { Header } from './components';

const useStyles = makeStyles(() => ({
  root: {}
}));

const Presentation = () => {
  const classes = useStyles();

  return (
    <Page
      className={classes.root}
      title="Home"
    >
      <Header />
    </Page>
  );
};

export default Presentation;
