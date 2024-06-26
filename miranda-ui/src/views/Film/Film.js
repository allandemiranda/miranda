/**
 * @description Film details
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
  Characters, 
  Planets,
  Alert,
  Species,
  Starships,
  Vehicles
} from 'components';
import { FilmInfo } from './components';

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

const Film = props => {
  const { match, history } = props;
  const classes = useStyles();
  const { id, tab } = match.params;

  const handleTabsChange = (event, value) => {
    history.push(value);
  };

  const tabs = [
    { value: 'summary', label: 'Summary' },
    { value: 'characters', label: 'Characters' },
    { value: 'planets', label: 'Planets'},
    { value: 'species', label: 'Species'},
    { value: 'starships', label: 'Starships'},
    { value: 'vehicles', label: 'Vehicles'}
  ];

  if (!tab) {
    return <Redirect to={`/film/${id}/summary`} />;
  }

  if (!tabs.find(t => t.value === tab)) {
    return <Redirect to="/errors/error-404" />;
  }

  const [film, setFilm] = useState();
  const [error, setError] = useState();

  useEffect(() => {
    let mounted = true;

    const fetchFilm = () => {
      axios.get('/films/'+ id +'/').then(response => {
        if (mounted) {
          setFilm(response.data); 
        }
      }).catch((error)=>{
        setError(error)
      });
    };

    fetchFilm();

    return () => {
      mounted = false;
    };
  }, []);

  if (!film) {
    if(error){
      return (
        <Page
          className={classes.root}
          title="Film Details"
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
      title="Film Details"
    >
      <Header 
        subTitle={'Film'}
        title={film.title} 
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
          component={<FilmInfo film={film} />}
        />}
        {tab === 'characters' && 
        <Characters 
          data={film} 
          title={'Film Characters'} 
        />}
        {tab === 'planets' && 
        <Planets 
          data={film}
          title={'Film Planets'}
        />}  
        {tab === 'species' && 
        <Species 
          data={film}
          title={'Film Species'}
        />}   
        {tab === 'starships' && 
        <Starships 
          data={film}
          title={'Film Starships'}
        />}  
        {tab === 'vehicles' && 
        <Vehicles 
          data={film}
          title={'Film Vehicles'}
        />}      
      </div>
    </Page>
  );
};

Film.propTypes = {
  history: PropTypes.object.isRequired,
  match: PropTypes.object.isRequired
};

export default Film;
