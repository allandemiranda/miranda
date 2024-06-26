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
 * A People resource is an individual person or character within the Star Wars universe
 * 
 * @param {Array} data A list of person url
 * @param {String} title The list title
 * 
 * @author Allan de Miranda
 */
const Pilots = props => {
  const { className, data, title, ...rest } = props;

  const classes = useStyles();
  const { history } = useRouter();

  const [people, setPeople] = useState([]);
  const [progress, setProgress] = useState(true);
  const [alertNull, setAlertNull] = useState(false);
  const [alertAxios, setAlertAxios] = useState({status: false, msg: ''});

  useEffect(async () => {
    let mounted = true;

    const fetchPeople = async () => {
      if(!data.pilots){
        setProgress(false);
        setAlertAxios({status: true, msg: 'Server Error'})
      } else {
        if(data.pilots.length > 0){
          const list_people = await data.pilots.map(async (url)=>{
            const response = await axios.get(url.split('/api')[1])
            return response
          })
          if (mounted) {
            const results = await Promise.all(list_people)
            setPeople(results);
          }
        } else {
          setProgress(false);
          setAlertNull(true);
        }
      }
    };

    await fetchPeople();

    return () => {
      mounted = false;
    };
  }, []);

  useEffect(()=>{
    if(people.length > 0){
      var errorMsg = 'Error!';
      for(var i=0; i<people.length; ++i){
        if(people[i].status !== 200){
          errorMsg = people[i].status;
          people.splice(i,1);
          --i;
        }
      }
      setProgress(false);
      if(people.length === 0){
        setAlertAxios({status: true, msg: errorMsg})
      }
    }
  },[people])

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
                    <TableCell>Mass</TableCell>
                    <TableCell>Height</TableCell>   
                    <TableCell>Gender</TableCell> 
                  </TableRow>
                </TableHead>
                <TableBody>
                  {people.map((person, key) => (
                    <TableRow 
                      hover
                      key={key}
                      onClick={() => history.push('/person' + person.data.url.split('people')[1] + 'summary')}
                      style={{cursor: 'pointer'}}
                    >
                      <TableCell>{person.data.name}</TableCell>
                      <TableCell>{person.data.mass} {' kilograms'}</TableCell>
                      <TableCell>{person.data.height} {' centimeters'}</TableCell>
                      <TableCell>{person.data.gender}</TableCell>
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

Pilots.propTypes = {
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

export default Pilots;