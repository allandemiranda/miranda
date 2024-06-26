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
 * A Film resource is a single film
 * 
 * @param {Array} data A list of film url
 * @param {String} title The list title
 * 
 * @author Allan de Miranda
 */
const Films = props => {
  const { className, data, title, ...rest } = props;

  const classes = useStyles();
  const { history } = useRouter();

  const [films, setFilms] = useState([]);
  const [progress, setProgress] = useState(true);
  const [alertNull, setAlertNull] = useState(false);
  const [alertAxios, setAlertAxios] = useState({status: false, msg: ''});

  useEffect(async () => {
    let mounted = true;

    const fetchFilms = async () => {
      if(!data.films){
        setProgress(false);
        setAlertAxios({status: true, msg: 'Server Error'})
      } else {
        if(data.films.length > 0){
          const list_films = await data.films.map(async (url)=>{
            const response = await axios.get(url.split('/api')[1])
            return response
          })
          if (mounted) {
            const results = await Promise.all(list_films)
            setFilms(results);
          }
        } else {
          setProgress(false);
          setAlertNull(true);
        }
      }
    };

    await fetchFilms();

    return () => {
      mounted = false;
    };
  }, []);

  useEffect(()=>{
    if(films.length > 0){
      var errorMsg = 'Error!';
      for(var i=0; i<films.length; ++i){
        if(films[i].status !== 200){
          errorMsg = films[i].status;
          films.splice(i,1);
          --i;
        }
      }
      setProgress(false);
      if(films.length === 0){
        setAlertAxios({status: true, msg: errorMsg})
      }
    }
  },[films])

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
                     <TableCell>Title</TableCell>
                     <TableCell>Director</TableCell>
                     <TableCell>Producer</TableCell>   
                   </TableRow>
                 </TableHead>
                 <TableBody>
                   {films.map((film, key) => (
                     <TableRow 
                       hover
                       key={key}
                       onClick={() => history.push('/film' + film.data.url.split('films')[1] + 'summary')}
                       style={{cursor: 'pointer'}}
                     >
                       <TableCell>{film.data.title}</TableCell>
                       <TableCell>{film.data.director}</TableCell>
                       <TableCell>{film.data.producer}</TableCell>
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

Films.propTypes = {
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

export default Films;
