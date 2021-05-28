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
// 
// Add one result
module.exports.addResult = async (event, context, callback) => {
    const reqBody = JSON.parse(event.body);
    const pollId = reqBody.PK;
    const resultGroups = reqBody.groups;

    // Get answers
    let answers = [];
    resultGroups.forEach(g => {
        let questions = g.questions;

        questions.forEach(q => {
          let answerId = uuidv4();

          let answer = {
            PutRequest: {
              Item: {
                PK: pollId,
                SK: answersId + '#' + answerId,
                type: q.type,
                label: q.label,
                value: q.value
              }
            }
          };
            answers.push({...answer});
        });
    });

    const resultId = uuidv4();
  
    const result = {
      PK: pollId,
      SK: resultsId + '#' + resultId,
      title: reqBody.title,
      description: reqBody.description,
      groups: reqBody.groups,
      createdAt: new Date().toISOString()
    };
  
    // Save poll
   let res = await db.put({TableName: table, Item: result}).promise().catch(err => {
      return callback(null, response(err.statusCode, err));
   });

   if (res) {
    // Save answers
      const params = {
          RequestItems: {
            [table]: answers
          }
      }
      res = await db.batchWrite(params).promise().catch(err => {
        return callback(null, response(err.statusCode, err))
      });
      return callback(null, response(200, res));
   } else {
      return callback(null, 'No se puedo crear la encuesta');
   }
  }