'use strict';

const AWS = require('aws-sdk')
const db = new AWS.DynamoDB.DocumentClient({})

const tableName = process.env.POLLS_TABLE_NAME

const response = (statusCode, message) => {
    return {
        statusCode: statusCode,
        body: JSON.stringify(message)
    }
}

const getPollByTitle = async (event) => {
    const title = event.queryStringParameters.title
    console.log('title: ', title)
  
    const params = {
      TableName: tableName,
      IndexName: 'SearchPollByTitle',
      KeyConditionExpression: 'title = :v AND begins_with(PK, :v1)',
      ExpressionAttributeValues: {
        ':v': title,
        ':v1': 'POLL'
      }
    }
  
    try {
      const pollSearch = await db.query(params).promise()
      console.log('pollSearch', pollSearch)
  
      if (!pollSearch.Items || pollSearch.Items.length !== 1) {
        return response(500, {
          message: "Poll not found"
        })
      }
  
      const poll = pollSearch.Items[0]
      
      return response(200, poll)
    } catch (err) {
      console.log('err: ', err)
      return response(err.statusCode, err)
    }
}

module.exports = {
    getPollByTitle
}