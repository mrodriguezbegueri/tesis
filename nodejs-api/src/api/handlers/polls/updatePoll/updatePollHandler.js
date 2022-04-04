'use strict';

const AWS = require('aws-sdk')
const db = new AWS.DynamoDB.DocumentClient({})

const tableName = process.env.POLLS_TABLE_NAME
const dynamoPollsId = process.env.POLLS_ID

const response = (statusCode, message) => {
    return {
        statusCode: statusCode,
        body: JSON.stringify(message)
    }
}

const updatePoll = async (event) => {
    const PK = event.pathParameters.id
    const reqBody = JSON.parse(event.body)
    const paramName = reqBody.paramName
    const paramValue = reqBody.paramValue
    
    const params = {
      TableName: tableName,
      Key: {
        PK: dynamoPollsId + '#' + PK,
        SK: dynamoPollsId + '#' + PK,
      },
      ConditionExpression: 'attribute_exists(PK) AND attribute_exists(SK)',
      UpdateExpression: 'set ' + paramName + ' = :v',
      ExpressionAttributeValues: {
        ':v': paramValue
      },
      ReturnValues: 'ALL_NEW'
    }
  
    try {
      const updatePoll = await db.update(params).promise()
      return response(200, updatePoll)
    } catch (err) {
      console.log('err: ', err)
      return response(500, 'Error updating the Poll')
    }
}

module.exports = {
    updatePoll
}