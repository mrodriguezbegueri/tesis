'use strict';

const AWS = require('aws-sdk')
const db = new AWS.DynamoDB.DocumentClient({})

const tableName = process.env.POLLS_TABLE_NAME

const { POLLS_ID, RESULTS_ID } = process.env

const response = (statusCode, message) => {
    return {
        statusCode: statusCode,
        body: JSON.stringify(message)
    }
}

const deleteResult = async (event) => {
    const PK = event.pathParameters.id
    const SK = event.queryStringParameters.pollId
    
    const params = {
      TableName: tableName,
      Key: {
        PK: RESULTS_ID + '#' + PK,
        SK: POLLS_ID + '#' + SK
      }
    }
  
    try {
      const deleteResult = await db.delete(params).promise()
      console.log('deleteResult: ', deleteResult)
      return response(200, 'Result with id: ' + PK + ' deleted successfully')
    } catch (err) {
      console.log(err)
      return response(500, 'Error deleting the Result')
    }
}


module.exports = {
    deleteResult
}