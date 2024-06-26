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

const FilmInfo = props => {
  const { film, className, ...rest } = props;

  const classes = useStyles();

  const [progress, setProgress] = useState(true);

  useEffect(()=>{
    if(film){
      setProgress(false)
    }
  },[film])

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
                  <TableCell>Episode</TableCell>
                  <TableCell>{film.episode_id}</TableCell>
                </TableRow>
                <TableRow >
                  <TableCell>Director</TableCell>
                  <TableCell>{film.director}</TableCell>
                </TableRow>
                <TableRow selected >
                  <TableCell>Opening Crawl</TableCell>
                  <TableCell>{film.opening_crawl}</TableCell>
                </TableRow>
                <TableRow >
                  <TableCell>Producer</TableCell>
                  <TableCell>{film.producer}</TableCell>
                </TableRow>
                <TableRow selected >
                  <TableCell>Release Date</TableCell>
                  <TableCell>{film.release_date}</TableCell>
                </TableRow>                                
              </TableBody>
            </Table>
          </CardContent>   
        </Card>}
    </div>
  );
};

FilmInfo.propTypes = {
  className: PropTypes.string,
  film: PropTypes.object.isRequired,  
};

export default FilmInfo;
