import React, { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import clsx from 'clsx';
import { makeStyles } from '@material-ui/styles';
import {
  Card,
  CardHeader,
  CardContent,
  Divider,
  Table,
  TableBody,
  TableRow,
  TableCell
} from '@material-ui/core';

import CircularProgress from '@material-ui/core/CircularProgress';

const useStyles = makeStyles(theme => ({
  root: {},
  content: {
    padding: 0
  },
  actions: {
    flexDirection: 'column',
    alignItems: 'flex-start',
    '& > * + *': {
      marginLeft: 0
    }
  },
  buttonIcon: {
    marginRight: theme.spacing(1)
  }
}));

const PlanetInfo = props => {
  const { planet, className, ...rest } = props;

  const classes = useStyles();

  const [progress, setProgress] = useState(true);

  useEffect(()=>{
    if(planet){
      setProgress(false)
    }
  },[planet])

  return (
    <div>
      {progress ? <CircularProgress/> : 
        <Card
          {...rest}
          className={clsx(classes.root, className)}
        >
          <CardHeader 
            
            title="Planet info" 
          />
          <Divider />
          <CardContent className={classes.content}>
            <Table>
              <TableBody>            
                <TableRow selected >
                  <TableCell>Climate</TableCell>
                  <TableCell>{planet.climate}</TableCell>
                </TableRow>
                <TableRow >
                  <TableCell>Diameter</TableCell>
                  <TableCell>{planet.diameter} {' kilometers'}</TableCell>
                </TableRow>
                <TableRow selected >
                  <TableCell>Gravity</TableCell>
                  <TableCell>{planet.gravity} {' standard G'}</TableCell>
                </TableRow>
                <TableRow >
                  <TableCell>Orbital Period</TableCell>
                  <TableCell>{planet.orbital_period} {' days'}</TableCell>
                </TableRow>
                <TableRow selected >
                  <TableCell>Population</TableCell>
                  <TableCell>{planet.population}</TableCell>
                </TableRow>
                <TableRow >
                  <TableCell>Rotation Period</TableCell>
                  <TableCell>{planet.rotation_period} {' hours'}</TableCell>
                </TableRow>
                <TableRow selected >
                  <TableCell>Surface Water</TableCell>
                  <TableCell>{planet.surface_water} {' percentage'}</TableCell>
                </TableRow>
                <TableRow >
                  <TableCell>Terrain</TableCell>
                  <TableCell>{planet.terrain}</TableCell>
                </TableRow>                              
              </TableBody>
            </Table>
          </CardContent>   
        </Card>}
    </div>
  );
};

PlanetInfo.propTypes = {
  className: PropTypes.string,
  planet: PropTypes.object.isRequired,  
};

export default PlanetInfo;
