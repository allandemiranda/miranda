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
 * A Species resource is a type of person or character within the Star Wars Universe
 * 
 * @param {Array} data A list of species url
 * @param {String} title The list title
 * 
 * @author Allan de Miranda
 */
const Species = props => {
  const { className, data, title, ...rest } = props;

  const classes = useStyles();
  const { history } = useRouter();

  const [species, setSpecies] = useState([]);
  const [progress, setProgress] = useState(true);
  const [alertNull, setAlertNull] = useState(false);
  const [alertAxios, setAlertAxios] = useState({status: false, msg: ''});

  useEffect(async () => {
    let mounted = true;

    const fetchSpecies = async () => {
      if(!data.species){
        setProgress(false);
        setAlertAxios({status: true, msg: 'Server Error'})
      } else {
        if(data.species.length > 0){
          const list_species = await data.species.map(async (url)=>{
            const response = await axios.get(url.split('/api')[1])
            return response
          })
          if (mounted) {
            const results = await Promise.all(list_species)
            setSpecies(results);
          }
        } else{
          setProgress(false);
          setAlertNull(true);
        }
      }
    };

    await fetchSpecies();

    return () => {
      mounted = false;
    };
  }, []);

  useEffect(()=>{
    if(species.length > 0){
      var errorMsg = 'Error!';
      for(var i=0; i<species.length; ++i){
        if(species[i].status !== 200){
          errorMsg = species[i].status;
          species.splice(i,1);
          --i;
        }
      }
      setProgress(false);
      if(species.length === 0){
        setAlertAxios({status: true, msg: errorMsg})
      }
    }
  },[species])

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
                     <TableCell>Language</TableCell>
                     <TableCell>Classification</TableCell>   
                     <TableCell>Designation</TableCell> 
                   </TableRow>
                 </TableHead>
                 <TableBody>
                   {species.map((specie, key) => (
                     <TableRow 
                       hover
                       key={key}
                       onClick={() => history.push('/specie' + specie.data.url.split('species')[1] + 'summary')}
                       style={{cursor: 'pointer'}}
                     >
                       <TableCell>{specie.data.name}</TableCell>
                       <TableCell>{specie.data.language}</TableCell>
                       <TableCell>{specie.data.classification}</TableCell>
                       <TableCell>{specie.data.designation}</TableCell>
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

Species.propTypes = {
  /**
   * Class component
   */
  className: PropTypes.string,
  /**
   * A list of species url
   */
  data: PropTypes.any.isRequired,
  /**
   * The list title
   */
  title: PropTypes.string.isRequired
};

export default Species;
