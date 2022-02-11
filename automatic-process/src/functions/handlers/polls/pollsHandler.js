const AWS = require('aws-sdk');
const db = new AWS.DynamoDB.DocumentClient({});
const pollsTableName = process.env.POLLS_TABLE_NAME;



const generateEventResponse = (processData) => {
    return {
        data: {
            poll: processData.poll,
            recordsSent: processData.recordsSent,
            recordsToSent: processData.recordsToSent
        }
    }
}
// Get one poll by title
const getPollByTitle = async (event, context, callback) => {
    console.log("event.data:", JSON.stringify(event))
    const title = event.data.title
    const recordsToSent = event.data.recordsToSent
    const recordsSent = event.data.recordsSent 

    console.log('title: ', title)
  
    const params = {
      TableName: pollsTableName,
      // IndexName: 'SearchPollByTitle',
      FilterExpression: 'title = :v1 AND begins_with(PK, :v2) AND begins_with(SK, :v2)',
      ExpressionAttributeValues: {
        ':v1': title,
        ':v2': 'POLL'
      }
    }
  
    try {
      const pollSearch = await db.scan(params).promise()
      console.log('pollSearch', pollSearch)
  
      if (!pollSearch.Items || pollSearch.Items.length !== 1) {
        throw new Error('Error getting poll')
      }
  
      const poll = pollSearch.Items[0]
      
      const eventResponseData = generateEventResponse({
        poll,
        recordsToSent,
        recordsSent    
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