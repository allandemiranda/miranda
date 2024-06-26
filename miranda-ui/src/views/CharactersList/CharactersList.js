/**
 * @description Character List
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

const CharactersList = () => {
  const classes = useStyles(); 

  return (
    <Page
      className={classes.root}
      title="Characters List"
    >
      <Header
        subTitle={'People'}
        title={'Characters'}
      />      
      <Results
        className={classes.results}                  
      />
    </Page>
  );
};

export default CharactersList;
