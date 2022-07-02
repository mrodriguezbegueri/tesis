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

const getForm = async (event) => {
    const PK = event.pathParameters.id
    console.log('PK: ', PK)
    
    const params = {
      TableName: tableName,
      Key: {
        PK: dynamoFormsId + '#' + PK,
        SK: dynamoFormsId + '#' + PK,
      }
    }
  
    try {
      const formSearch = await db.get(params).promise()
      console.log('formSearch: ', formSearch)
  
      if (!formSearch.Item) {
        return response(404, 'Form with id: ' + PK + 'not found')
      }
  
      const form = formSearch.Item
      return response(200, form)
    } catch (err) {
      console.log('err: ', err)
      return response(500, 'Error getting the Form')
    }
}

module.exports = {
    getForm
}