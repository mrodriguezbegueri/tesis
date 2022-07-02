'use strict';

const AWS = require('aws-sdk')
const db = new AWS.DynamoDB.DocumentClient({})
const { v4: uuidv4 } = require('uuid')

const tableName = process.env.FORMS_TABLE_NAME
const dynamoformsId = process.env.FORMS_ID

const response = (statusCode, message) => {
    return {
        statusCode: statusCode,
        body: JSON.stringify(message)
    }
}

const createForm = async (event) => {
    const reqBody = JSON.parse(event.body)

    const formId = uuidv4()
  
    const form = {
      PK: dynamoformsId + '#' + formId,
      SK: dynamoformsId + '#' + formId,
      GSI1PK: 'FORMS',
      title: reqBody.title,
      description: reqBody.description,
      groups: reqBody.groups
    }

    const params = {
      TableName: tableName,
      Item: form
    }

    try {
      const createFormDB = await db.put(params).promise()
      console.log('createFormDB: ', createFormDB)

      return response(200, form)
    } catch (err) {
      console.log('err: ', err)
      return response(500, 'Error creating the Form')
    }
}

module.exports = {
  createForm
}