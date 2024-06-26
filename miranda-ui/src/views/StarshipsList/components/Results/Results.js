import React, { useState, useEffect } from 'react';
import clsx from 'clsx';
import PropTypes from 'prop-types';
import PerfectScrollbar from 'react-perfect-scrollbar';
import { makeStyles } from '@material-ui/styles';
import {
  Card,
  CardActions,
  CardContent,
  Divider,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TablePagination,
  TableRow
} from '@material-ui/core';
import { Alert } from 'components';
import axios from 'utils/axios';
import useRouter from 'utils/useRouter';
import CircularProgress from '@material-ui/core/CircularProgress';

const useStyles = makeStyles(theme => ({
  root: {},
  content: {
    padding: 0
  },
  inner: {
    minWidth: 700
  },
  nameCell: {
    display: 'flex',
    alignItems: 'center'
  },
  avatar: {
    height: 42,
    width: 42,
    marginRight: theme.spacing(1)
  },
  actions: {
    padding: theme.spacing(1),
    justifyContent: 'flex-end'
  }
}));

const Results = props => {
  const { className, ...rest } = props;

  const classes = useStyles();
  const { history } = useRouter();

  const [starships, setStarships] = useState([]);
  const [page, setPage] = useState(0); 
  const [nextPage, setNextPage] = useState(true);  
  const [progress, setProgress] = useState(true);
  const [alertNull, setAlertNull] = useState(false);
  const [alertAxios, setAlertAxios] = useState({status: false, msg: ''});

  const handleChangePage = (event, page) => {
    setPage(page);
  };

  useEffect(() => {
    let mounted = true;

    const fetchStarships = () => {
      setStarships([]);
      setProgress(true);
      axios.get('/starships/?page='+(page+1)).then(response => {
        if (mounted) {          
          setProgress(false);
          if(response.data.results.length > 0){
            setStarships(response.data.results);
            if(response.data.next === null){
              setNextPage(false)
            } else {
              setNextPage(true)
            }
          } else {
            setAlertNull(true);
          }
        }
      }).catch((err)=>{
        setProgress(false);
        setAlertAxios({status: true, msg: err.message + ' !'})
      });
    };

    fetchStarships();

    return () => {
      mounted = false;
    };
  }, [page]);

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
                      onClick={() => history.push('/starship' + starship.url.split('starships')[1] + 'summary')}
                      style={{cursor: 'pointer'}}
                    >                      
                      <TableCell>{starship.name}</TableCell>
                      <TableCell>{starship.model}</TableCell>
                      <TableCell>{starship.manufacturer}</TableCell>
                      <TableCell>{starship.MGLT}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </div>
          </PerfectScrollbar>
        </CardContent>
        <CardActions className={classes.actions}>
          <TablePagination            
            component="div"
            count={-1} 
            labelDisplayedRows={()=>{false}}
            nextIconButtonProps={{
              style: {
                color: !nextPage ? '#b5b8c4' : null, 
                cursor: !nextPage ? 'not-allowed' : null,
                pointerEvents: !nextPage ? 'none' : null
              }
            }}
            onChangePage={handleChangePage}
            page={page}            
            rowsPerPage={-1}
            rowsPerPageOptions={[]}                       
          />
        </CardActions>
      </Card>}
    </div>
  );
};

Results.propTypes = {
  className: PropTypes.string  
};

Results.defaultProps = {
  characters: []
};

export default Results;
