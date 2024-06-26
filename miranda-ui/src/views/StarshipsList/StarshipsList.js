/**
 * @description Starships List
 * 
 * @author Allan de Miranda
 */

import React from 'react';
import { makeStyles } from '@material-ui/styles';
import { Page, Header } from 'components';
import { Results } from './components';

const useStyles = makeStyles(theme => ({
  root: {
    padding: theme.spacing(3)
  },
  results: {
    marginTop: theme.spacing(3)
  }
}));

const StarshipsList = () => {
  const classes = useStyles(); 

  return (
    <Page
      className={classes.root}
      title="Starships List"
    >
      <Header
        subTitle={'Starships'}
        title={'Starships'}
      />
      <Results 
        className={classes.results}
      />
    </Page>
  );
};

export default StarshipsList;
