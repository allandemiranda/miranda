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

const SpecieInfo = props => {
  const { specie, className, ...rest } = props;

  const classes = useStyles();

  const [progress, setProgress] = useState(true);

  useEffect(()=>{
    if(specie){
      setProgress(false)
    }
  },[specie])

  return (
    <div>
      {progress ? <CircularProgress/> : 
        <Card
          {...rest}
          className={clsx(classes.root, className)}
        >
          <CardHeader 
            
            title="Specie info" 
          />
          <Divider />
          <CardContent className={classes.content}>
            <Table>
              <TableBody>            
                <TableRow selected >
                  <TableCell>Average Height</TableCell>
                  <TableCell>{specie.average_height} {' centimeters'}</TableCell>
                </TableRow>
                <TableRow >
                  <TableCell>Average Lifespan</TableCell>
                  <TableCell>{specie.average_lifespan} {' years'}</TableCell>
                </TableRow>
                <TableRow selected >
                  <TableCell>Designation</TableCell>
                  <TableCell>{specie.designation}</TableCell>
                </TableRow>
                <TableRow >
                  <TableCell>Eye Colors</TableCell>
                  <TableCell>{specie.eye_colors}</TableCell>
                </TableRow>
                <TableRow selected >
                  <TableCell>Hair Colors</TableCell>
                  <TableCell>{specie.hair_colors}</TableCell>
                </TableRow>
                <TableRow >
                  <TableCell>Language</TableCell>
                  <TableCell>{specie.language}</TableCell>
                </TableRow>
                <TableRow selected >
                  <TableCell>Skin Colors</TableCell>
                  <TableCell>{specie.skin_colors}</TableCell>
                </TableRow>                                        
              </TableBody>
            </Table>
          </CardContent>   
        </Card>}
    </div>
  );
};

SpecieInfo.propTypes = {
  className: PropTypes.string,
  specie: PropTypes.object.isRequired,  
};

export default SpecieInfo;
