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

const StarshipInfo = props => {
  const { starship, className, ...rest } = props;

  const classes = useStyles();

  const [progress, setProgress] = useState(true);

  useEffect(()=>{
    if(starship){
      setProgress(false)
    }
  },[starship])

  return (
    <div>
      {progress ? <CircularProgress/> : 
        <Card
          {...rest}
          className={clsx(classes.root, className)}
        >
          <CardHeader 
            
            title="Starship info" 
          />
          <Divider />
          <CardContent className={classes.content}>
            <Table>
              <TableBody>            
                <TableRow selected >
                  <TableCell>MGLT</TableCell>
                  <TableCell>{starship.MGLT}</TableCell>
                </TableRow>
                <TableRow>
                  <TableCell>Cargo Capacity</TableCell>
                  <TableCell>{starship.cargo_capacity} {' kilograms'}</TableCell>
                </TableRow>
                <TableRow selected >
                  <TableCell>Consumables</TableCell>
                  <TableCell>{starship.consumables}</TableCell>
                </TableRow>
                <TableRow>
                  <TableCell>Cost in Credits</TableCell>
                  <TableCell>{starship.cost_in_credits} {' galactic credits'}</TableCell>
                </TableRow>
                <TableRow selected >
                  <TableCell>Consumables</TableCell>
                  <TableCell>{starship.consumables}</TableCell>
                </TableRow>
                <TableRow>
                  <TableCell>Crew</TableCell>
                  <TableCell>{starship.crew} {' personnel needed'}</TableCell>
                </TableRow>
                <TableRow selected >
                  <TableCell>Hyperdrive Rating</TableCell>
                  <TableCell>{starship.hyperdrive_rating}</TableCell>
                </TableRow>
                <TableRow>
                  <TableCell>Length</TableCell>
                  <TableCell>{starship.length} {' meters'}</TableCell>
                </TableRow>
                <TableRow selected >
                  <TableCell>Manufacturer</TableCell>
                  <TableCell>{starship.manufacturer}</TableCell>
                </TableRow>
                <TableRow>
                  <TableCell>Max Atmosphering Speed</TableCell>
                  <TableCell>{starship.max_atmosphering_speed === 'n/a' ? 'incapable of atmospheric flight' : starship.max_atmosphering_speed + ' meters'}</TableCell>
                </TableRow>
                <TableRow selected >
                  <TableCell>Model</TableCell>
                  <TableCell>{starship.model}</TableCell>
                </TableRow>
                <TableRow>
                  <TableCell>Passengers</TableCell>
                  <TableCell>{starship.passengers} {' people'}</TableCell>
                </TableRow>
                <TableRow selected >
                  <TableCell>Starship Class</TableCell>
                  <TableCell>{starship.starship_class}</TableCell>
                </TableRow>
              </TableBody>
            </Table>
          </CardContent>   
        </Card>}
    </div>
  );
};

StarshipInfo.propTypes = {
  className: PropTypes.string,
  starship: PropTypes.object.isRequired,  
};

export default StarshipInfo;
