'use strict';

const AWS = require('aws-sdk')
const db = new AWS.DynamoDB.DocumentClient({})

const { POLLS_TABLE_NAME } = process.env

const response = (statusCode, message) => {
    return {
        statusCode: statusCode,
        body: JSON.stringify(message)
    }
}

const createResult = async (event) => {
  try {
  
    const result = JSON.parse(event.body)

    console.log(JSON.stringify(result))

    let params = {
      TableName: POLLS_TABLE_NAME,
      Item: result
    }

    let res = await db.put(params).promise()

    if (res) {
        return response(200, result)
    } else {
        return response(500, 'Error at creating the result')
    }
  } catch (err) {
    console.error('err: ', err)
    return response(err.statusCode, err)
  }
}

module.exports = {
  createResult
}