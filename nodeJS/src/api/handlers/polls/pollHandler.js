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
module.exports.createPoll = (event) => {
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
    };
  
    return db.put({
      TableName: tableName,
      Item: poll
    })
    .promise()
    .then(() => {
      return response(201, poll)
    })
    .catch(err => {
      return response(err.statusCode, err)
    });
  }

// Update one poll
module.exports.updatePoll = (event) => {
  const PK = event.pathParameters.id;
  const reqBody = JSON.parse(event.body);
  const paramName = reqBody.paramName;
  const paramValue = reqBody.paramValue;
  
  const params = {
    TableName: table,
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

  return db.update(params)
  .promise()
  .then(res => {
    return response(200, res)
  })
  .catch(err => {
    return response(err.statusCode, err)
  })
}

// Get one poll
module.exports.getPoll = (event) => {
  const PK = event.pathParameters.id;
  
  const params = {
    TableName: tableName,
    Key: {
      PK: dynamoPollsId + '#' + PK,
      SK: dynamoPollsId + '#' + PK,
    }
  };

  return db.get(params)
    .promise()
    .then(res => {
      if (res.Item)
        return response(200,res.Item)
      else
        return response(404, {error: 'Poll not found'})
    })
    .catch(err => {
      return response(err.statusCode, err)
    })
}

// Delete one poll
module.exports.deletePoll = (event) => {
  const PK = event.pathParameters.id;
  
  const params = {
    TableName: tableName,
    Key: {
      PK: dynamoPollsId + '#' + PK,
      SK: dynamoPollsId + '#' + PK
    }
  };

  return db.delete(params)
  .promise()
  .then(() => {
    return response(200, { message: 'Poll deleted successfully' })
  })
  .catch(err => {
    return response(err.statusCode, err)
  })
}

// Get list of poll's titles
module.exports.getAllPolls = () => {
  const params = {
    TableName: tableName,
    IndexName: 'ListPollsTitles',
    KeyConditionExpression: 'GSI1PK = :v',
    ExpressionAttributeValues: {
      ':v': 'POLLS'
    }
  };

  return db.query(params)
  .promise()
  .then(res => {
    return response(200, res)
  })
  .catch(err => {
    return response(err.statusCode, err)
  })
}

// Get one poll by title
module.exports.getPollByTitle = (event) => {
  const title = event.pathParameters.title
  
  const params = {
    TableName: tableName,
    IndexName: 'SearchPollByTitle',
    KeyConditionExpression: 'title = :v1 AND begins_with(SK, :v2)',
    ExpressionAttributeValues: {
      ':v1': title,
      ':v2': 'POLL'
    }
  };

  return db.query(params)
  .promise()
  .then(res => {
    return response(200, res)
  })
  .catch(err => {
    return response(err.statusCode, err)
  })
}
