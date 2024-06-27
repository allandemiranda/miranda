import React, { useState, useEffect } from 'react';
import PropTypes from 'prop-types';
import { makeStyles } from '@material-ui/styles';
import axios from 'utils/axios';
import {
  Page,
  Header
} from 'components';
import MaterialTable from 'material-table';

const useStyles = makeStyles(theme => ({
  root: {
    padding: theme.spacing(3)
  },
  results: {
    marginTop: theme.spacing(3)
  }
}));

const TradesList = (props) => {
  const classes = useStyles();

  const { match } = props;
  const { id } = match.params;

  const [trades, setTrades] = useState();

  const columns = [
    { value: 'scope.timeFrame', label: 'TimeFrame' },
    { value: 'stopLoss', label: 'Stop Loss', type: 'numeric' },
    { value: 'takeProfit', label: 'Take Profit', type: 'numeric'},
    { value: 'spreadMax', label: 'Spread Max', type: 'numeric'},
    { value: 'slotWeek', label: 'Week'},
    { value: 'slotStart', label: 'Start'},
    { value: 'slotEnd', label: 'End'},
    { value: 'balance', label: 'Balance'},
    { value: 'isActivate', label: 'Activate'}
  ];

  useEffect(() => {

    let mounted = true;

    const fetchTrades = () => {
      axios.get('/trades/'+id, {
        timeout: 120000
      }).then(response => {
        if (mounted) {
          let data = response.data;
          console.log(data);
          setTrades(data);
        }
      }).catch((err)=> {
        console.log(err);
      });
    };

    fetchTrades();

    return () => {
      mounted = false;
    };
  }, []);

  return (
    <Page
      className={classes.root}
      title="Trade List"
    >
      <Header
        subTitle={'test'}
        title={'test'}
      />
      <MaterialTable
        columns={[...columns]}
        data={trades}
        title="Simple Action Preview"
      />
    </Page>
  );
};

TradesList.propTypes = {
  id: PropTypes.string.isRequired,
  match: PropTypes.object.isRequired
};

export default TradesList;
