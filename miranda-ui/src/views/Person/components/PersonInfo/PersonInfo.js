import React, { useState, useEffect } from 'react';
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
import axios from 'utils/axios';

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

const PersonInfo = props => {
  const { person, className, ...rest } = props;

  const classes = useStyles();

  const [homeworld, setHomeworld] = useState();
  const [progress, setProgress] = useState(true);

  useEffect(() => {
    let mounted = true;

    const fetchHomeworld = () => {
      const homeworld_url = person.homeworld.split('api/')[1];      
      axios.get(homeworld_url).then(response => {
        if (mounted) {          
          setHomeworld(response.data);
          setProgress(false);
        }
      });
    };

    fetchHomeworld();

    return () => {
      mounted = false;
    };
  }, []);

  return (
    <div>
      {progress ? <CircularProgress/> :
        <Card
          {...rest}
          className={clsx(classes.root, className)}
        >
          <CardHeader 
            
            title="Person info"       
          />
          <Divider />
          <CardContent className={classes.content}>
            <Table>
              <TableBody>            
                <TableRow selected >
                  <TableCell>Birthday</TableCell>
                  <TableCell>{person.birth_year}</TableCell>
                </TableRow>
                <TableRow>
                  <TableCell>Eye Color</TableCell>
                  <TableCell>{person.eye_color}</TableCell>
                </TableRow>
                <TableRow selected>
                  <TableCell>Gender</TableCell>
                  <TableCell>{person.gender}</TableCell>
                </TableRow>
                <TableRow>
                  <TableCell>Hair Color</TableCell>
                  <TableCell>{person.hair_color}</TableCell>
                </TableRow>
                <TableRow selected>
                  <TableCell>Height</TableCell>
                  <TableCell>{person.height}{' centimeters'}</TableCell>
                </TableRow>
                <TableRow>
                  <TableCell>Homeworld</TableCell>
                  <TableCell>{homeworld.name}</TableCell>
                </TableRow>
                <TableRow selected>
                  <TableCell>Mass</TableCell>
                  <TableCell>{person.mass}{' kilograms'}</TableCell>
                </TableRow>
                <TableRow>
                  <TableCell>Skin Color</TableCell>
                  <TableCell>{person.skin_color}</TableCell>
                </TableRow>
              </TableBody>
            </Table>
          </CardContent>   
        </Card>}
    </div>
  );
};

PersonInfo.propTypes = {
  className: PropTypes.string,
  person: PropTypes.object.isRequired,  
};

export default PersonInfo;
