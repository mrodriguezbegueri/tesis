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

const getAllForms = async () => {
    const params = {
      TableName: tableName,
      IndexName: 'ListFormsTitles',
      KeyConditionExpression: 'GSI1PK = :v',
      ExpressionAttributeValues: {
        ':v': 'FORMS'
      }
    }
  
    try {
      const formsSearch = await db.query(params).promise()
      const forms = formsSearch.Items
      return response(200, forms)
    } catch (err) {
      return response(err.statusCode, err)
    }
}

module.exports = {
  getAllForms
}