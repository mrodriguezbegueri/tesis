'use strict';

const AWS = require('aws-sdk')
const db = new AWS.DynamoDB.DocumentClient({})

const tableName = process.env.FORMS_TABLE_NAME
const dynamoFormsId = process.env.FORMS_ID

const response = (statusCode, message) => {
    return {
        statusCode: statusCode,
        body: JSON.stringify(message)
    }
}

const deleteForm = async (event) => {
    const PK = event.pathParameters.id
    
    const params = {
      TableName: tableName,
      Key: {
        PK: dynamoFormsId + '#' + PK,
        SK: dynamoFormsId + '#' + PK
      }
    }
  
    try {
      const deleteFormDB = await db.delete(params).promise()
      console.log('deleteFormDB: ', deleteFormDB)
      return response(200, 'Form with id: ' + PK + ' deleted successfully')
    } catch (err) {
      return response(500, 'Error deleting the Form')
    }
}

module.exports = {
  deleteForm
}