/**
 * @description Planet details
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
  Residents,
  Alert
} from 'components';
import { PlanetInfo } from './components';

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

const Planet = props => {
  const { match, history } = props;
  const classes = useStyles();
  const { id, tab } = match.params;

  const handleTabsChange = (event, value) => {
    history.push(value);
  };

  const tabs = [
    { value: 'summary', label: 'Summary' },
    { value: 'films', label: 'Films' },
    { value: 'residents', label: 'Residents'}
  ];

  if (!tab) {
    return <Redirect to={`/planet/${id}/summary`} />;
  }

  if (!tabs.find(t => t.value === tab)) {
    return <Redirect to="/errors/error-404" />;
  }

  const [planet, setPlanet] = useState();
  const [error, setError] = useState();

  useEffect(() => {
    let mounted = true;

    const fetchPlanet = () => {
      axios.get('/planets/'+ id +'/').then(response => {
        if (mounted) {
          setPlanet(response.data); 
        }
      }).catch((error)=>{
        setError(error)
      });
    };

    fetchPlanet();

    return () => {
      mounted = false;
    };
  }, []);

  if (!planet) {
    if(error){
      return (
        <Page
          className={classes.root}
          title="Planet Details"
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
      title="Planet Details"
    >
      <Header 
        subTitle={'Planet'}
        title={planet.name} 
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
          component={<PlanetInfo planet={planet} />}
        />}
        {tab === 'films' && 
        <Films 
          data={planet} 
          title={'Planet Films'} 
        />}
        {tab === 'residents' && 
        <Residents 
          data={planet}
          title={'Planet Residents'}
        />}        
      </div>
    </Page>
  );
};

Planet.propTypes = {
  history: PropTypes.object.isRequired,
  match: PropTypes.object.isRequired
};

export default Planet;
