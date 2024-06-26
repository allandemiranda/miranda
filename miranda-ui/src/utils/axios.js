import axios from 'axios';

// eslint-disable-next-line no-undef
const https = require('https');

const agent = new https.Agent({
  rejectUnauthorized: false,
});

const instance = axios.create({
  baseURL: 'https://swapi.dev/api/',
  responseType: 'json',
  httpsAgent: agent,
  crossDomain: true
});

export default instance;
