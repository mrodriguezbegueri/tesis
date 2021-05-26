'use strict';

const AWS = require('aws-sdk');
const db = new AWS.DynamoDB.DocumentClient({});
const { v4: uuidv4 } = require('uuid');

const table = process.env.TABLE_NAME;
const resultsId = process.env.RESULTS_ID;
const pollsId = process.env.POLLS_ID;
const answersId = process.env.ANSWER_ID

// Create a response
const response = (statusCode, message) => {
    return {
        statusCode: statusCode,
        body: JSON.stringify(message)
    };
  }

// Add one result
module.exports.addResult = (event, context, callback) => {
    const reqBody = JSON.parse(event.body);
    const pollId = reqBody.pollId;
    const resultGroups = reqBody.groups;

    // Get answers
    let answers = [];
    resultGroups.forEach(g => {
        let questions = g.questions;

        questions.forEach(q => {

            let answer = {
                PutRequest: {
                    Item: {
                        PK: pollsId + '#' + pollId,
                        SK: answersId + '#' + answersId,
                        type: q.type,
                        label: q.label,
                        value: q.value
                    }
                }
            };

            answers.push({...answer});
        })
    
    });

    const resultId = uuidv4();
  
    const result = {
      PK: pollsId + '#' + pollId,
      SK: resultsId + '#' + resultId,
      title: reqBody.title,
      description: reqBody.description,
      groups: reqBody.groups,
      createdAt: new Date().toISOString()
    };
  
    return db.put({
      TableName: table,
      Item: result
    })
    .promise()
    .then(() => {

        // Save answers
        const params = {
            RequestItems = {}
        }
        params.RequestItems[table] = answers;

        db.batchWrite(params)
        .promise()
        .then(res => {
          callback(null, response(200, res));
        })
        .catch(err => callback(null, response(err.statusCode, err)))

      callback(null, response(201, poll));
    })
    .catch(err => response(null, response(err.statusCode, err)));
  }