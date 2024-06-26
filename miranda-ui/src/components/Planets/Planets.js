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
 * A Planet resource is a large mass, planet or planetoid in the Star Wars Universe, at the time of 0 ABY
 * 
 * @param {Array} data A list of planet url
 * @param {String} title The list title
 * 
 * @author Allan de Miranda
 */
const Planets = props => {
  const { className, data, title, ...rest } = props;

  const classes = useStyles();
  const { history } = useRouter();

  const [planets, setPlanets] = useState([]);
  const [progress, setProgress] = useState(true);
  const [alertNull, setAlertNull] = useState(false);
  const [alertAxios, setAlertAxios] = useState({status: false, msg: ''});

  useEffect(async () => {
    let mounted = true;

    const fetchPlanets = async () => {
      if(!data.planets){
        setProgress(false);
        setAlertAxios({status: true, msg: 'Server Error'})
      } else {
        if(data.planets.length > 0){
          const list_planets = await data.planets.map(async (url)=>{
            const response = await axios.get(url.split('/api')[1])
            return response
          })
          if (mounted) {
            const results = await Promise.all(list_planets)
            setPlanets(results);
          }
        } else {
          setProgress(false);
          setAlertNull(true);
        }
      }
    };

    await fetchPlanets();

    return () => {
      mounted = false;
    };
  }, []);

  useEffect(()=>{
    if(planets.length > 0){
      var errorMsg = 'Error!';
      for(var i=0; i<planets.length; ++i){
        if(planets[i].status !== 200){
          errorMsg = planets[i].status;
          planets.splice(i,1);
          --i;
        }
      }
      setProgress(false);
      if(planets.length === 0){
        setAlertAxios({status: true, msg: errorMsg})
      }
    }
  },[planets])

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
                     <TableCell>Climate</TableCell>
                     <TableCell>Diameter</TableCell>  
                     <TableCell>Population</TableCell>  
                   </TableRow>
                 </TableHead>
                 <TableBody>
                   {planets.map((planet, key) => (
                     <TableRow 
                       hover
                       key={key}
                       onClick={() => history.push('/planet' + planet.data.url.split('planets')[1] + 'summary')}
                       style={{cursor: 'pointer'}}
                     >
                       <TableCell>{planet.data.name}</TableCell>
                       <TableCell>{planet.data.climate}</TableCell>
                       <TableCell>{planet.data.diameter} {' kilometers'}</TableCell>
                       <TableCell>{planet.data.population}</TableCell>
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

Planets.propTypes = {
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

export default Planets;
