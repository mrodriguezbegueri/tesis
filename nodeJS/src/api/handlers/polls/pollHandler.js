'use strict';

const AWS = require('aws-sdk');
const db = new AWS.DynamoDB.DocumentClient({});
const { v4: uuidv4 } = require('uuid');

const tableName = process.env.POLLS_TABLE_NAME;
const dynamoPollsId = process.env.POLLS_ID;

// Create a response
const response = (statusCode, message) => {
    return {
        statusCode: statusCode,
        body: JSON.stringify(message)
    };
  }

// Create one Poll
module.exports.createPoll = async (event) => {
    const reqBody = JSON.parse(event.body);

    const pollId = uuidv4();
  
    const poll = {
      PK: dynamoPollsId + '#' + pollId,
      SK: dynamoPollsId + '#' + pollId,
      GSI1PK: 'POLLS',
      title: reqBody.title,
      description: reqBody.description,
      groups: reqBody.groups,
      createdAt: new Date().toISOString()
    }

    const params = {
      TableName: tableName,
      Item: poll
    }

    try {
      const createPoll = await db.put(params).promise()
      console.log('createPoll: ', createPoll)

      delete params.Item
      params['Key'] = {
        PK: dynamoPollsId + '#' + pollId,
        SK: dynamoPollsId + '#' + pollId,
      }

      const getPoll = await db.get(params).promise()

      if (!getPoll.Item) {
        return response(404, {error: 'Error creating Poll'})
      }

      return response(201, getPoll.Item)
    } catch (err) {
      console.log('err: ', err)
      return response(err.statusCode, err)
    }
  }

// Update one poll
module.exports.updatePoll = async (event) => {
  const PK = event.pathParameters.id;
  const reqBody = JSON.parse(event.body);
  const paramName = reqBody.paramName;
  const paramValue = reqBody.paramValue;
  
  const params = {
    TableName: tableName,
    Key: {
      PK: dynamoPollsId + '#' + PK,
      SK: dynamoPollsId + '#' + PK,
    },
    ConditionExpression: 'attribute_exists(PK) AND attribute_exists(SK)',
    UpdateExpression: 'set ' + paramName + ' = :v',
    ExpressionAttributeValues: {
      ':v': paramValue
    },
    ReturnValue: 'ALL_NEW'
  };

  try {
    const updatePoll = await db.update(params).promise()
    return response(200, updatePoll)
  } catch (err) {
    console.log('err: ', err)
    return response(err.statusCode, err)
  }
}

// Get one poll
module.exports.getPoll = async (event) => {
  const PK = event.pathParameters.id
  console.log('PK: ', PK)
  
  const params = {
    TableName: tableName,
    Key: {
      PK: dynamoPollsId + '#' + PK,
      SK: dynamoPollsId + '#' + PK,
    }
  };

  try {
    const pollSearch = await db.get(params).promise()
    console.log('poll: ', pollSearch)

    if (!pollSearch.Item) {
      return response(404, {error: 'Poll not found'})
    }

    const poll = pollSearch.Item
    return response(200, poll)
  } catch (err) {
    console.log('err: ', err)
    return response(err.statusCode, err)
  }
}

// Delete one poll
module.exports.deletePoll = async (event) => {
  const PK = event.pathParameters.id;
  
  const params = {
    TableName: tableName,
    Key: {
      PK: dynamoPollsId + '#' + PK,
      SK: dynamoPollsId + '#' + PK
    }
  };

  try {
    const deletePoll = await db.delete(params).promise()
    console.log('deletePoll: ', deletePoll)
    return response(200, { message: 'Poll deleted successfully' })
  } catch (err) {
    return response(err.statusCode, err)
  }
}

// Get list of poll's titles
module.exports.getAllPolls = async () => {
  const params = {
    TableName: tableName,
    IndexName: 'ListPollsTitles',
    KeyConditionExpression: 'GSI1PK = :v',
    ExpressionAttributeValues: {
      ':v': 'POLLS'
    }
  };

  try {
    const pollsSearch = await db.query(params).promise()
    const polls = pollsSearch.Items
    return response(200, polls)
  } catch (err) {
    return response(err.statusCode, err)
  }
}

// Get one poll by title
module.exports.getPollByTitle = async (event) => {
  const title = event.queryStringParameters.title
  console.log('title: ', title)

  const params = {
    TableName: tableName,
    // IndexName: 'SearchPollByTitle',
    FilterExpression: 'title = :v1 AND begins_with(PK, :v2)',
    ExpressionAttributeValues: {
      ':v1': title,
      ':v2': 'POLL'
    }
  }

  try {
    const pollSearch = await db.scan(params).promise()
    console.log('pollSearch', pollSearch)

    if (!pollSearch.Items || pollSearch.Items.length !== 1) {
      return response(500, {
        message: "Error getting the poll"
      })
    }

    const poll = pollSearch.Items[0]
    
    return response(200, poll)
  } catch (err) {
    console.log('err: ', err)
    return response(err.statusCode, err)
  }
}
