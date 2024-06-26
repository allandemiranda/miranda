/**
 * @description Starship details
 * 
 * @author Allan de Miranda
 */

import React, { useState, useEffect } from 'react';
import { Redirect } from 'react-router-dom';
import PropTypes from 'prop-types';
import { makeStyles } from '@material-ui/styles';
import { Tabs, Tab, Divider, colors } from '@material-ui/core';
import axios from 'utils/axios';
import { 
  Page, 
  Header, 
  Summary, 
  Films, 
  Pilots,
  Alert
} from 'components';
import { StarshipInfo } from './components';

const useStyles = makeStyles(theme => ({
  root: {
    padding: theme.spacing(3)
  },
  tabs: {
    marginTop: theme.spacing(3)     
  },
  divider: {
    backgroundColor: colors.grey[300]
  },
  content: {
    marginTop: theme.spacing(3)
  }
}));

const Starship = props => {
  const { match, history } = props;
  const classes = useStyles();
  const { id, tab } = match.params;

  const handleTabsChange = (event, value) => {
    history.push(value);
  };

  const tabs = [
    { value: 'summary', label: 'Summary' },
    { value: 'films', label: 'Films' },
    { value: 'pilots', label: 'Pilots'}
  ];

  if (!tab) {
    return <Redirect to={`/starship/${id}/summary`} />;
  }

  if (!tabs.find(t => t.value === tab)) {
    return <Redirect to="/errors/error-404" />;
  }

  const [starship, setStarship] = useState();
  const [error, setError] = useState();

  useEffect(() => {
    let mounted = true;

    const fetchStarship = () => {
      axios.get('/starships/'+ id +'/').then(response => {
        if (mounted) {
          setStarship(response.data); 
        }
      }).catch((error)=>{
        setError(error)
      });
    };

    fetchStarship();

    return () => {
      mounted = false;
    };
  }, []);

  if (!starship) {
    if(error){
      return (
        <Page
          className={classes.root}
          title="Starship Details"
        >
          <Alert
            message={error.message}
            variant={'error'}
          />
        </Page>);
    } else {
      return null;
    }
  }

  return (      
    <Page
      className={classes.root}
      title="Starship Details"
    >
      <Header 
        subTitle={'Starship'}
        title={starship.name} 
      />
      <Tabs
        className={classes.tabs}
        onChange={handleTabsChange}
        scrollButtons="auto"
        value={tab}
        variant="scrollable"
      >
        {tabs.map(tab => (
          <Tab
            key={tab.value}
            label={tab.label}
            value={tab.value}
          />
        ))}
      </Tabs>
      <Divider className={classes.divider} />
      <div className={classes.content}>
        {tab === 'summary' && 
        <Summary 
          component={<StarshipInfo starship={starship} />}
        />}
        {tab === 'films' && 
        <Films 
          data={starship} 
          title={'Starship Films'} 
        />}
        {tab === 'pilots' && 
        <Pilots 
          data={starship}
          title={'Starship Pilots'}
        />}        
      </div>
    </Page>
  );
};

Starship.propTypes = {
  history: PropTypes.object.isRequired,
  match: PropTypes.object.isRequired
};

export default Starship;
