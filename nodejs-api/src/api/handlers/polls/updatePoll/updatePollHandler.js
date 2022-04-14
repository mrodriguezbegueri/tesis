'use strict';

const AWS = require('aws-sdk')
const db = new AWS.DynamoDB.DocumentClient({})

const { POLLS_ID, POLLS_TABLE_NAME } = process.env


const response = (statusCode, message) => {
    return {
        statusCode: statusCode,
        body: JSON.stringify(message)
    }
}

const updatePoll = async (event) => {
    const PK = event.pathParameters.id
    const poll = JSON.parse(event.body)
    
    let toUpdatePropers = []
    let ExpressionAttributeNames = {}
    let ExpressionAttributeValues = {}

    for (const property in poll) {
      
      toUpdatePropers.push(`#${property} = :${property}`)
      ExpressionAttributeNames['#' + property] = property 
      
      let pollProperty = poll[property]

      ExpressionAttributeValues[':' + property] = pollProperty
    }

    const UpdateExpression = `set ${toUpdatePropers.join(',')}`

    console.log('UpdateExpression: ', UpdateExpression)
    console.log('ExpressionAttributeNames: ', ExpressionAttributeNames)
    console.log('ExpressionAttributeValues: ', ExpressionAttributeValues)
    
    const params = {
      TableName: POLLS_TABLE_NAME,
      Key: {
        PK: POLLS_ID + '#' + PK,
        SK: POLLS_ID + '#' + PK,
      },
      ConditionExpression: 'attribute_exists(PK) AND attribute_exists(SK)',
      
      UpdateExpression,
      ExpressionAttributeNames,
      ExpressionAttributeValues,
      ReturnValues: 'ALL_NEW'
    }
  
    try {
      let updatePoll = await db.update(params).promise()
      updatePoll = updatePoll.Attributes

      return response(200, updatePoll)
    } catch (err) {
      console.log('err: ', err)
      return response(500, 'Error updating the Poll')
    }
}

module.exports = {
    updatePoll
}