import React, { useState, useEffect } from 'react';
import clsx from 'clsx';
import PropTypes from 'prop-types';
import PerfectScrollbar from 'react-perfect-scrollbar';
import { makeStyles } from '@material-ui/styles';
import {
  Card,
  CardHeader,
  CardContent,
  Divider,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow
} from '@material-ui/core';
import useRouter from 'utils/useRouter';
import axios from 'utils/axios';
import { Alert } from 'components';
import CircularProgress from '@material-ui/core/CircularProgress';

const useStyles = makeStyles(() => ({
  root: {},
  content: {
    padding: 0
  }
}));

/**
 * A Vehicle resource is a single transport craft that does not have hyperdrive capability
 * 
 * @param {Array} data A list of vehicle url
 * @param {String} title The list title
 * 
 * @author Allan de Miranda
 */
const Vehicles = props => {
  const { className, data, title, ...rest } = props;

  const classes = useStyles();
  const { history } = useRouter();

  const [vehicles, setVehicles] = useState([]);
  const [progress, setProgress] = useState(true);
  const [alertNull, setAlertNull] = useState(false);
  const [alertAxios, setAlertAxios] = useState({status: false, msg: ''});

  useEffect(async () => {
    let mounted = true;

    const fetchVehicles = async () => {
      if(!data.vehicles){
        setProgress(false);
        setAlertAxios({status: true, msg: 'Server Error'})
      } else {
        if(data.vehicles.length > 0){
          const list_vehicles = await data.vehicles.map(async (url)=>{
            const response = await axios.get(url.split('/api')[1])
            return response
          })
          if (mounted) {
            const results = await Promise.all(list_vehicles)
            setVehicles(results);
          }
        } else {
          setProgress(false);
          setAlertNull(true);
        }
      }
    };

    await fetchVehicles();

    return () => {
      mounted = false;
    };
  }, []);

  useEffect(()=>{
    if(vehicles.length > 0){
      var errorMsg = 'Error!';
      for(var i=0; i<vehicles.length; ++i){
        if(vehicles[i].status !== 200){
          errorMsg = vehicles[i].status;
          vehicles.splice(i,1);
          --i;
        }
      }
      setProgress(false);
      if(vehicles.length === 0){
        setAlertAxios({status: true, msg: errorMsg})
      }
    }
  },[vehicles])

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
      
      {alertNull ? 
        <Alert
          message={'There is no information here!'}
          variant={'warning'}
        /> : null }

      {progress ? <CircularProgress/> : !alertNull && !alertAxios.status &&
      <Card>
        <CardHeader
          
          title={title}
        />
        <Divider />
        <CardContent className={classes.content}>
          <PerfectScrollbar>
            <div className={classes.inner}>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>Name</TableCell>
                    <TableCell>Model</TableCell>
                    <TableCell>Manufacturer</TableCell>   
                    <TableCell>Class</TableCell> 
                  </TableRow>
                </TableHead>
                <TableBody>
                  {vehicles.map((vehicle, key) => (
                    <TableRow 
                      hover
                      key={key}
                      onClick={() => history.push('/vehicle' + vehicle.data.url.split('vehicles')[1] + 'summary')}
                      style={{cursor: 'pointer'}}
                    >
                      <TableCell>{vehicle.data.name}</TableCell>
                      <TableCell>{vehicle.data.model}</TableCell>
                      <TableCell>{vehicle.data.manufacturer}</TableCell>
                      <TableCell>{vehicle.data.vehicle_class}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </div>
          </PerfectScrollbar>
        </CardContent>
      </Card>}
    </div>
  );
};

Vehicles.propTypes = {
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

export default Vehicles;
