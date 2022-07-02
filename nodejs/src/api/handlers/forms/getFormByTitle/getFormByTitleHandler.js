'use strict';

const AWS = require('aws-sdk')
const db = new AWS.DynamoDB.DocumentClient({})

const tableName = process.env.FORMS_TABLE_NAME

const response = (statusCode, message) => {
    return {
        statusCode: statusCode,
        body: JSON.stringify(message)
    }
}

const getFormByTitle = async (event) => {
    const title = event.queryStringParameters.title
    console.log('title: ', title)
  
    const params = {
      TableName: tableName,
      IndexName: 'SearchFormByTitle',
      KeyConditionExpression: 'title = :v AND begins_with(PK, :v1)',
      ExpressionAttributeValues: {
        ':v': title,
        ':v1': 'FORM'
      }
    }
  
    try {
      const formSearch = await db.query(params).promise()
      console.log('formSearch', formSearch)
  
      if (!formSearch.Items || formSearch.Items.length !== 1) {
        return response(500, {
          message: "Form not found"
        })
      }
  
      const form = formSearch.Items[0]
      
      return response(200, form)
    } catch (err) {
      console.log('err: ', err)
      return response(err.statusCode, err)
    }
}

module.exports = {
    getFormByTitle
}