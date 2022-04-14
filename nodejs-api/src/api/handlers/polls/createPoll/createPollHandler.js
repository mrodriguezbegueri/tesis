'use strict';

const AWS = require('aws-sdk')
const db = new AWS.DynamoDB.DocumentClient({})
const { v4: uuidv4 } = require('uuid')

const tableName = process.env.POLLS_TABLE_NAME
const dynamoPollsId = process.env.POLLS_ID

const response = (statusCode, message) => {
    return {
        statusCode: statusCode,
        body: JSON.stringify(message)
    }
}

const createPoll = async (event) => {
    const reqBody = JSON.parse(event.body)

    const pollId = uuidv4()
  
    const poll = {
      PK: dynamoPollsId + '#' + pollId,
      SK: dynamoPollsId + '#' + pollId,
      GSI1PK: 'POLLS',
      title: reqBody.title,
      description: reqBody.description,
      groups: reqBody.groups
    }

    const params = {
      TableName: tableName,
      Item: poll
    }

    try {
      const createPoll = await db.put(params).promise()
      console.log('createPoll: ', createPoll)

      return response(200, poll)
    } catch (err) {
      console.log('err: ', err)
      return response(500, 'Error creating the Poll')
    }
}

module.exports = {
    createPoll
}