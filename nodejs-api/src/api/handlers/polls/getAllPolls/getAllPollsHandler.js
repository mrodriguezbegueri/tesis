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

const getAllPolls = async () => {
    const params = {
      TableName: tableName,
      IndexName: 'ListPollsTitles',
      KeyConditionExpression: 'GSI1PK = :v',
      ExpressionAttributeValues: {
        ':v': 'POLLS'
      }
    }
  
    try {
      const pollsSearch = await db.query(params).promise()
      const polls = pollsSearch.Items
      return response(200, polls)
    } catch (err) {
      return response(err.statusCode, err)
    }
}

module.exports = {
    getAllPolls
}