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

const getPoll = async (event) => {
    const PK = event.pathParameters.id
    console.log('PK: ', PK)
    
    const params = {
      TableName: tableName,
      Key: {
        PK: dynamoPollsId + '#' + PK,
        SK: dynamoPollsId + '#' + PK,
      }
    }
  
    try {
      const pollSearch = await db.get(params).promise()
      console.log('poll: ', pollSearch)
  
      if (!pollSearch.Item) {
        return response(404, 'Poll with id: ' + PK + 'not found')
      }
  
      const poll = pollSearch.Item
      return response(200, poll)
    } catch (err) {
      console.log('err: ', err)
      return response(500, 'Error getting the Poll')
    }
}

module.exports = {
    getPoll
}