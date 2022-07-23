'use strict';

const AWS = require('aws-sdk')
const db = new AWS.DynamoDB.DocumentClient({})


const { FORMS_ID, RESULTS_ID, FORMS_TABLE_NAME } = process.env

const response = (statusCode, message) => {
    return {
        statusCode: statusCode,
        body: JSON.stringify(message)
    }
}

const deleteResult = async (event) => {

  console.log("event: ", JSON.stringify(event))
    const PK = event.pathParameters.id
    const SK = event.queryStringParameters.formId
    
    const params = {
      TableName: FORMS_TABLE_NAME,
      Key: {
        PK: RESULTS_ID + '#' + PK,
        SK: FORMS_ID + '#' + SK
      }
    }

    console.log("params: ", params)
  
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