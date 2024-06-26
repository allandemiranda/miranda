/**
 * @description Vehicle details
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
import { VehicleInfo } from './components';

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

const Vehicle = props => {
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
    return <Redirect to={`/vehicle/${id}/summary`} />;
  }

  if (!tabs.find(t => t.value === tab)) {
    return <Redirect to="/errors/error-404" />;
  }

  const [vehicle, setVehicle] = useState();
  const [error, setError] = useState();

  useEffect(() => {
    let mounted = true;

    const fetchVehicle = () => {
      axios.get('/vehicles/'+ id +'/').then(response => {
        if (mounted) {
          setVehicle(response.data); 
        }
      }).catch((error)=>{
        setError(error)
      });
    };

    fetchVehicle();

    return () => {
      mounted = false;
    };
  }, []);

  if (!vehicle) {
    if(error){
      return (
        <Page
          className={classes.root}
          title="Vehicle Details"
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
      title="Vehicle Details"
    >
      <Header 
        subTitle={'Vehicle'}
        title={vehicle.name} 
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
          component={<VehicleInfo vehicle={vehicle} />}
        />}
        {tab === 'films' && 
        <Films 
          data={vehicle} 
          title={'Vehicle Films'} 
        />}
        {tab === 'pilots' && 
        <Pilots 
          data={vehicle}
          title={'Vehicle Pilots'}
        />}        
      </div>
    </Page>
  );
};

Vehicle.propTypes = {
  history: PropTypes.object.isRequired,
  match: PropTypes.object.isRequired
};

export default Vehicle;
