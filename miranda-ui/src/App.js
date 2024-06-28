import React from 'react';
import { Router } from 'react-router-dom';
import { renderRoutes } from 'react-router-config';

import routes from './routes';

const App = () => {
  return(
    <Router history={history}>
      {renderRoutes(routes)}
    </Router>
  );
}

export default App;