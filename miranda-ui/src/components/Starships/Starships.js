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
 * A Starship resource is a single transport craft that has hyperdrive capability
 * 
 * @param {Array} data A list of starship url
 * @param {String} title The list title
 * 
 * @author Allan de Miranda
 */
const Starships = props => {
  const { className, data, title, ...rest } = props;

  const classes = useStyles();
  const { history } = useRouter();

  const [starships, setStarships] = useState([]);
  const [progress, setProgress] = useState(true);
  const [alertNull, setAlertNull] = useState(false);
  const [alertAxios, setAlertAxios] = useState({status: false, msg: ''});

  useEffect(async () => {
    let mounted = true;

    const fetchStarships = async () => {
      if(!data.starships){
        setProgress(false);
        setAlertAxios({status: true, msg: 'Server Error'})
      } else {
        if(data.starships.length > 0){
          const list_starships = await data.starships.map(async (url)=>{
            const response = await axios.get(url.split('/api')[1])
            return response
          })
          if (mounted) {
            const results = await Promise.all(list_starships)
            setStarships(results);
          }
        } else {
          setProgress(false);
          setAlertNull(true);
        }
      }
    };

    await fetchStarships();

    return () => {
      mounted = false;
    };
  }, []);

  useEffect(()=>{
    if(starships.length > 0){
      var errorMsg = 'Error!';
      for(var i=0; i<starships.length; ++i){
        if(starships[i].status !== 200){
          errorMsg = starships[i].status;
          starships.splice(i,1);
          --i;
        }
      }
      setProgress(false);
      if(starships.length === 0){
        setAlertAxios({status: true, msg: errorMsg})
      }
    }
  },[starships])

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
                    <TableCell>MGLT</TableCell> 
                  </TableRow>
                </TableHead>
                <TableBody>
                  {starships.map((starship, key) => (
                    <TableRow 
                      hover
                      key={key}
                      onClick={() => history.push('/starship' + starship.data.url.split('starships')[1] + 'summary')}
                      style={{cursor: 'pointer'}}
                    >
                      <TableCell>{starship.data.name}</TableCell>
                      <TableCell>{starship.data.model}</TableCell>
                      <TableCell>{starship.data.manufacturer}</TableCell>
                      <TableCell>{starship.data.MGLT}</TableCell>
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

Starships.propTypes = {
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

export default Starships;
