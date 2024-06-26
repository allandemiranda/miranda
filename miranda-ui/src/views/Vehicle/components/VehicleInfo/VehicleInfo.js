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

const VehicleInfo = props => {
  const { vehicle, className, ...rest } = props;

  const classes = useStyles();

  const [progress, setProgress] = useState(true);

  useEffect(()=>{
    if(vehicle){
      setProgress(false)
    }
  },[vehicle])

  return (
    <div>
      {progress ? <CircularProgress/> : 
        <Card
          {...rest}
          className={clsx(classes.root, className)}
        >
          <CardHeader 
            
            title="Vehicle info" 
          />
          <Divider />
          <CardContent className={classes.content}>
            <Table>
              <TableBody>            
                <TableRow selected >
                  <TableCell>Cargo Capacity</TableCell>
                  <TableCell>{vehicle.cargo_capacity} {' kilograms'}</TableCell>
                </TableRow>
                <TableRow >
                  <TableCell>Consumables</TableCell>
                  <TableCell>{vehicle.consumables}</TableCell>
                </TableRow>
                <TableRow selected >
                  <TableCell>Cost in Credits</TableCell>
                  <TableCell>{vehicle.cost_in_credits} {' Galactic Credits'}</TableCell>
                </TableRow>
                <TableRow >
                  <TableCell>Crew</TableCell>
                  <TableCell>{vehicle.crew} {' personnel'}</TableCell>
                </TableRow>
                <TableRow selected >
                  <TableCell>Length</TableCell>
                  <TableCell>{vehicle.length} {' meters'}</TableCell>
                </TableRow>
                <TableRow >
                  <TableCell>Max Atmosphering Speed</TableCell>
                  <TableCell>{vehicle.length} {' meters'}</TableCell>
                </TableRow>
                <TableRow selected >
                  <TableCell>Model</TableCell>
                  <TableCell>{vehicle.model}</TableCell>
                </TableRow>
                <TableRow >
                  <TableCell>passengers</TableCell>
                  <TableCell>{vehicle.passengers}</TableCell>
                </TableRow>
                <TableRow selected >
                  <TableCell>Vehicle Class</TableCell>
                  <TableCell>{vehicle.vehicle_class}</TableCell>
                </TableRow>                
              </TableBody>
            </Table>
          </CardContent>   
        </Card>}
    </div>
  );
};

VehicleInfo.propTypes = {
  className: PropTypes.string,
  vehicle: PropTypes.object.isRequired,  
};

export default VehicleInfo;
