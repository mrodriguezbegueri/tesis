const AWS = require('aws-sdk');
const db = new AWS.DynamoDB.DocumentClient({});
const pollsTableName = process.env.POLLS_TABLE_NAME;



const generateEventResponse = (processData) => {
    return {
            poll: processData.poll
    }
}
// Get one poll by title
const getPollByTitle = async (event, context, callback) => {
    console.log("event.data:", JSON.stringify(event))
    const title = event.data.title

    console.log('title: ', title)
  
    const params = {
      TableName: pollsTableName,
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
        throw new Error('Error getting poll')
      }
  
      const poll = pollSearch.Items[0]
      
      const eventResponseData = generateEventResponse({
        poll
    })
        
      console.log('eventResponseData: ', eventResponseData) 
      return eventResponseData

    } catch (err) {
      console.log('err: ', err)
    }
  }

module.exports = {
    getPollByTitle
}