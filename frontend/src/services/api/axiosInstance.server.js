const axios = require('axios');
require('dotenv').config();

const axiosInstanceServer = axios.create({
  baseURL: process.env.API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

module.exports = { axiosInstanceServer };
