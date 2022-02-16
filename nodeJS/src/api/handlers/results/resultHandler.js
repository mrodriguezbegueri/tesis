'use strict';

const AWS = require('aws-sdk');
const db = new AWS.DynamoDB.DocumentClient({});
const { v4: uuidv4 } = require('uuid');

const tableName = process.env.POLLS_TABLE_NAME;
const dynamoResultsId = process.env.RESULTS_ID;
const dynamoAnswersId = process.env.ANSWER_ID

const response = (statusCode, message) => {
    return {
        statusCode: statusCode,
        body: JSON.stringify(message)
    };
}

const addResult = async (event) => {
  const reqBody = JSON.parse(event.body);
  const pollId = reqBody.PK;
  const resultGroups = reqBody.groups;

  // Get answers
  let answers = [];

  resultGroups.forEach(group => {
      let questions = group.questions;

      questions.forEach(question => {
        let answerId = uuidv4();

        let answer = {
          PutRequest: {
            Item: {
              PK: pollId,
              SK: dynamoAnswersId + '#' + answerId,
              type: question.type,
              label: question.label,
              value: question.value
            }
          }
        };
          answers.push({...answer});
      });
  });

  const resultId = uuidv4();

  const result = {
    PK: pollId,
    SK: dynamoResultsId + '#' + resultId,
    title: reqBody.title,
    description: reqBody.description,
    groups: reqBody.groups,
    createdAt: new Date().toISOString()
  };

  // Save poll
  try {
    let res = await db.put({TableName: tableName, Item: result}).promise()

    if (res) {
      // Save answers
        const params = {
            RequestItems: {
              [tableName]: answers
            }
        }
        res = await db.batchWrite(params).promise()
        return response(200, res)
    } else {
        return response(500, { message: 'Error at creating the poll' })
    }
  } catch (err) {
    return response(err.statusCode, err)
  }
}


module.exports = {
  addResult
}