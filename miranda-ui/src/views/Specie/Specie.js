/**
 * @description Specie details
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
  People,
  Alert,
  Homeworld
} from 'components';
import { SpecieInfo } from './components';

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

const Specie = props => {
  const { match, history } = props;
  const classes = useStyles();
  const { id, tab } = match.params;

  const handleTabsChange = (event, value) => {
    history.push(value);
  };

  const tabs = [
    { value: 'summary', label: 'Summary' },
    { value: 'films', label: 'Films' },
    { value: 'homeworld', label: 'Homeworld'},
    { value: 'people', label: 'People' }
  ];

  if (!tab) {
    return <Redirect to={`/specie/${id}/summary`} />;
  }

  if (!tabs.find(t => t.value === tab)) {
    return <Redirect to="/errors/error-404" />;
  }

  const [specie, setSpecie] = useState();
  const [error, setError] = useState();

  useEffect(() => {
    let mounted = true;

    const fetchSpecie = () => {
      axios.get('/species/'+ id +'/').then(response => {
        if (mounted) {
          setSpecie(response.data); 
        }
      }).catch((error)=>{
        setError(error)
      });
    };

    fetchSpecie();

    return () => {
      mounted = false;
    };
  }, []);

  if (!specie) {
    if(error){
      return (
        <Page
          className={classes.root}
          title="Specie Details"
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
      title="Specie Details"
    >
      <Header 
        subTitle={'Specie'}
        title={specie.name} 
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
          component={<SpecieInfo specie={specie} />}
        />}
        {tab === 'films' && 
        <Films 
          data={specie} 
          title={'Specie Films'} 
        />}
        {tab === 'homeworld' && 
        <Homeworld 
          data={specie} 
          title={'Specie Homeworld'} 
        />}
        {tab === 'people' && 
        <People 
          data={specie}
          title={'Specie People'}
        />}        
      </div>
    </Page>
  );
};

Specie.propTypes = {
  history: PropTypes.object.isRequired,
  match: PropTypes.object.isRequired
};

export default Specie;
