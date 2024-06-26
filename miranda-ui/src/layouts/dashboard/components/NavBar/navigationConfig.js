/* eslint-disable react/no-multi-comp */
/* eslint-disable react/display-name */
import PeopleIcon from '@material-ui/icons/PeopleOutlined';
import FlightIcon from '@material-ui/icons/Flight';

export default [
  {
    title: 'Pages',
    pages: [
      {
        title: 'Characters',
        href: '/characters',
        icon: PeopleIcon
      },
      {
        title: 'Starships',
        href: '/starships',
        icon: FlightIcon
      }
    ]
  }
];
