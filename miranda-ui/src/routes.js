/* eslint-disable react/no-multi-comp */
/* eslint-disable react/display-name */
import React, { lazy } from 'react';
import { Redirect } from 'react-router-dom';

import ErrorLayout from './layouts/Error';
import DashboardLayout from './layouts/Dashboard';
import PresentationView from './views/Presentation';

const routes = [
  {
    path: '/',
    exact: true,
    component: () => <Redirect to="/presentation" />
  },
  {
    path: '/errors',
    component: ErrorLayout,
    routes: [
      {
        path: '/errors/error-401',
        exact: true,
        component: lazy(() => import('views/Error401'))
      },
      {
        path: '/errors/error-404',
        exact: true,
        component: lazy(() => import('views/Error404'))
      },
      {
        path: '/errors/error-500',
        exact: true,
        component: lazy(() => import('views/Error500'))
      },
      {
        component: () => <Redirect to="/errors/error-404" />
      }
    ]
  },
  {
    route: '*',
    component: DashboardLayout,
    routes: [
      {
        path: '/trades/:id',
        exact: true,
        component: lazy(()=> import('views/TradeList'))
      },
      {
        path: '/specie/:id/:tab',
        exact: true,
        component: lazy(()=> import('views/Specie'))
      },
      {
        path: '/planet/:id/:tab',
        exact: true,
        component: lazy(()=> import('views/Planet'))
      },
      {
        path: '/film/:id/:tab',
        exact: true,
        component: lazy(()=> import('views/Film'))
      },
      {
        path: '/vehicle/:id/:tab',
        exact: true,
        component: lazy(()=> import('views/Vehicle'))
      },
      {
        path: '/starship/:id/:tab',
        exact: true,
        component: lazy(()=> import('views/Starship'))
      },
      {
        path: '/person/:id/:tab',
        exact: true,
        component: lazy(()=> import('views/Person'))
      },
      {
        path: '/characters',
        exact: true,
        component: lazy(()=> import('views/CharactersList'))
      },
      {
        path: '/starships',
        exact: true,
        component: lazy(()=> import('views/StarshipsList'))
      },
      {
        path: '/presentation',
        exact: true,
        component: PresentationView
      },
      {
        component: () => <Redirect to="/errors/error-404" />
      }
    ]
  }
];

export default routes;
