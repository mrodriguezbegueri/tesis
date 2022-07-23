'use strict';

const AWS = require('aws-sdk')
const db = new AWS.DynamoDB.DocumentClient({})

const { FORMS_TABLE_NAME, RESULTS_ID } = process.env


const response = (statusCode, message) => {
    return {
        statusCode: statusCode,
        body: JSON.stringify(message)
    }
}

const updateResult = async (event) => {
    let PK = event.pathParameters.id
    PK = `${RESULTS_ID}#${PK}`

    const result = JSON.parse(event.body)

    const SK = result.SK
    
    let toUpdatePropers = []
    let ExpressionAttributeNames = {}
    let ExpressionAttributeValues = {}

    for (const property in result) {

      if (property === 'PK' || property === 'SK') {
          continue
      }
      
      toUpdatePropers.push(`#${property} = :${property}`)
      ExpressionAttributeNames['#' + property] = property 
      
      let resultProperty = result[property]

      ExpressionAttributeValues[':' + property] = resultProperty
    }

    const UpdateExpression = `set ${toUpdatePropers.join(',')}`

    console.log('UpdateExpression: ', UpdateExpression)
    console.log('ExpressionAttributeNames: ', ExpressionAttributeNames)
    console.log('ExpressionAttributeValues: ', ExpressionAttributeValues)

    console.log('PK: ', PK)
    console.log('SK: ', SK)
    
    const params = {
      TableName: FORMS_TABLE_NAME,
      Key: {
        PK,
        SK
      },
      ConditionExpression: 'attribute_exists(PK) AND attribute_exists(SK)',
      
      UpdateExpression,
      ExpressionAttributeNames,
      ExpressionAttributeValues,
      ReturnValues: 'ALL_NEW'
    }
  
    try {
      let updateResult = await db.update(params).promise()
      updateResult = updateResult.Attributes
      
      return response(200, updateResult)
    } catch (err) {
      console.log('err: ', err)
      return response(500, 'Error updating the Result')
    }
}

module.exports = {
    updateResult
}