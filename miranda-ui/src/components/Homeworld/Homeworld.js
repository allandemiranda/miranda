import React, { useState, useEffect } from 'react';
import clsx from 'clsx';
import PropTypes from 'prop-types';
import { makeStyles } from '@material-ui/styles';
import {
  Card,
  CardHeader,
  CardContent,
  Divider,
  Table,
  TableBody,
  TableCell,
  TableRow
} from '@material-ui/core';
import axios from 'utils/axios';
import { Alert } from 'components';
import CircularProgress from '@material-ui/core/CircularProgress';
import useRouter from 'utils/useRouter';

const useStyles = makeStyles(() => ({
  root: {},
  content: {
    padding: 0
  }
}));

/**
 * A Planet resource is a large mass, planet or planetoid in the Star Wars Universe, at the time of 0 ABY
 * 
 * @param {Array} data A list of planet url
 * @param {String} title The list title
 * 
 * @author Allan de Miranda
 */
const Homeworld = props => {
  const { className, data, title, ...rest } = props;

  const { history } = useRouter();
  const classes = useStyles();

  const [homeworld, setHomeworld] = useState([]);
  const [progress, setProgress] = useState(true);
  const [alertAxios, setAlertAxios] = useState({status: false, msg: ''});

  useEffect(async () => {
    let mounted = true;

    const fetchHomeworld = async () => {  
      if(!data.homeworld){
        setProgress(false);
        setAlertAxios({status: true, msg: 'Server Error'})
      } else {
        const response = await axios.get(data.homeworld.split('/api')[1]).catch((err)=>{
          setProgress(false);
          setAlertAxios({status: true, msg: err.message});
          mounted = false;
        })
        if (mounted) {
          const results = await Promise.all([response]);
          setProgress(false);
          if(results[0].status === 200){            
            setHomeworld(results[0].data);
          }
        }
      }
    };

    await fetchHomeworld();

    return () => {
      mounted = false;
    };
  }, []);

  return (
    <div
      {...rest}
      className={clsx(classes.root, className)}
    >
      {alertAxios.status ? 
        <Alert
          message={alertAxios.msg}
          variant={'error'}
        /> : null }

      {progress ? <CircularProgress/> : !alertAxios.status &&
      <Card>
        <CardHeader
          
          title={title}
        />
        <Divider />
        <CardContent className={classes.content}>
          <Table>
            <TableBody>            
              <TableRow
                onClick={() => history.push('/planet' + homeworld.url.split('planets')[1] + 'summary')}
                selected
                style={{cursor: 'pointer'}}
              >
                <TableCell>Name</TableCell>
                <TableCell>{homeworld.name}</TableCell>
              </TableRow>
              <TableRow>
                <TableCell>Climate</TableCell>
                <TableCell>{homeworld.climate}</TableCell>
              </TableRow>
              <TableRow selected>
                <TableCell>Diameter</TableCell>
                <TableCell>{homeworld.diameter} {' kilometers'}</TableCell>
              </TableRow>
              <TableRow>
                <TableCell>Gravity</TableCell>
                <TableCell>{homeworld.gravity} {' standard G'}</TableCell>
              </TableRow>
              <TableRow selected>
                <TableCell>Orbital Period</TableCell>
                <TableCell>{homeworld.orbital_period} {' hours'}</TableCell>
              </TableRow>
              <TableRow>
                <TableCell>Population</TableCell>
                <TableCell>{homeworld.population}</TableCell>
              </TableRow>
              <TableRow selected>
                <TableCell>Rotation Period</TableCell>
                <TableCell>{homeworld.rotation_period} {' days'}</TableCell>
              </TableRow>
              <TableRow>
                <TableCell>Surface Water</TableCell>
                <TableCell>{homeworld.surface_water}{' percentage of the planet surface'}</TableCell>
              </TableRow>
              <TableRow selected>
                <TableCell>Terrain</TableCell>
                <TableCell>{homeworld.terrain}</TableCell>
              </TableRow>
            </TableBody>
          </Table>
        </CardContent>        
      </Card>}
    </div>
  );
};

Homeworld.propTypes = {
  /**
   * Class component
   */
  className: PropTypes.string,
  /**
   * A list of person url
   */
  data: PropTypes.any.isRequired,
  /**
   * The list title
   */
  title: PropTypes.string.isRequired
};

export default Homeworld;
