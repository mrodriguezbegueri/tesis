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

const deletePoll = async (event) => {
    const PK = event.pathParameters.id
    
    const params = {
      TableName: tableName,
      Key: {
        PK: dynamoPollsId + '#' + PK,
        SK: dynamoPollsId + '#' + PK
      }
    }
  
    try {
      const deletePoll = await db.delete(params).promise()
      console.log('deletePoll: ', deletePoll)
      return response(200, 'Poll with id: ' + PK + 'deleted successfully')
    } catch (err) {
      return response(500, 'Error deleting the Poll')
    }
}

module.exports = {
    deletePoll
}