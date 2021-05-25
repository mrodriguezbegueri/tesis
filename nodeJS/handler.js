'use strict';

const AWS = require('aws-sdk');
const db = new AWS.DynamoDB.DocumentClient({});
const { v4: uuidv4 } = require('uuid');

const table = process.env.TABLE_NAME;
const pollsId = process.env.POLLS_ID;

// Create a response
const response = (statusCode, message) => {
    return {
        statusCode: statusCode,
        body: JSON.stringify(message)
    };
  }


  module.exports.createPoll = (event, context, callback) => {
    const reqBody = JSON.parse(event.body);

    const pollId = uuidv4();
  
    const poll = {
      PK: pollsId + '#' + pollId,
      SK: pollsId + '#' + pollId,
      GSI1PK: 'POLLS',
      title: reqBody.title,
      description: reqBody.description,
      groups: reqBody.groups,
      createdAt: new Date().toISOString()
    };
  
    return db.put({
      TableName: table,
      Item: poll
    })
    .promise()
    .then(() => {
      callback(null, response(201, poll));
    })
    .catch(err => response(null, response(err.statusCode, err)));
  
  }