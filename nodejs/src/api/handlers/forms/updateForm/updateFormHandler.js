'use strict';

const AWS = require('aws-sdk')
const db = new AWS.DynamoDB.DocumentClient({})

const { FORMS_ID, FORMS_TABLE_NAME } = process.env


const response = (statusCode, message) => {
    return {
        statusCode: statusCode,
        body: JSON.stringify(message)
    }
}

const updateForm = async (event) => {
    const PK = event.pathParameters.id
    const form = JSON.parse(event.body)
    
    let toUpdatePropers = []
    let ExpressionAttributeNames = {}
    let ExpressionAttributeValues = {}

    for (const property in form) {
      
      toUpdatePropers.push(`#${property} = :${property}`)
      ExpressionAttributeNames['#' + property] = property 
      
      let formProperty = form[property]

      ExpressionAttributeValues[':' + property] = formProperty
    }

    const UpdateExpression = `set ${toUpdatePropers.join(',')}`

    console.log('UpdateExpression: ', UpdateExpression)
    console.log('ExpressionAttributeNames: ', ExpressionAttributeNames)
    console.log('ExpressionAttributeValues: ', ExpressionAttributeValues)
    
    const params = {
      TableName: FORMS_TABLE_NAME,
      Key: {
        PK: FORMS_ID + '#' + PK,
        SK: FORMS_ID + '#' + PK,
      },
      ConditionExpression: 'attribute_exists(PK) AND attribute_exists(SK)',
      
      UpdateExpression,
      ExpressionAttributeNames,
      ExpressionAttributeValues,
      ReturnValues: 'ALL_NEW'
    }
  
    try {
      let updateFormDB = await db.update(params).promise()
      updateFormDB = updateFormDB.Attributes

      return response(200, updateFormDB)
    } catch (err) {
      console.log('err: ', err)
      return response(500, 'Error updating the Form')
    }
}

module.exports = {
    updateForm
}