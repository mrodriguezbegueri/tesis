const AWS = require('aws-sdk');
const db = new AWS.DynamoDB.DocumentClient({});
const { v4: uuidv4 } = require('uuid');

// const tableName = process.env.POLLS_TABLE_NAME;

const { POLLS_TABLE_NAME, RESULTS_ID, QUESTIONS_ID, GROUPS_ID } = process.env


const response = (statusCode, message) => {
    return {
        statusCode: statusCode,
        body: JSON.stringify(message)
    }
}

const dynamoSaver = async (event) => {

    try {
        console.info(JSON.stringify(event))

        // const messages = event.Records
        const results = event.Records
        // const result = messages[0].body

        const res = await saveResult(results)

        return res
    } catch(err) {
        console.error('error: ', err)
        throw new Error(err.message)
    }
}

const saveResult = async (results) => {
  try {
    
      const resultsToSave = []
      results.forEach(result => {
         let parsedResult = JSON.parse(result.body)
         console.info('parsedResult: ', parsedResult)
         
         let dynamoItem = {
           PutRequest: {
             Item: parsedResult
           }
         }
         
         resultsToSave.push(dynamoItem)
      })

      console.info('resultsToSave: ', resultsToSave)

      const params = {
        RequestItems: {
          [POLLS_TABLE_NAME]: resultsToSave
        }
      }

      const batchWriteResponse = await db.batchWrite(params).promise()
  
      // let res = await db.put({TableName: POLLS_TABLE_NAME, Item: result}).promise()
  
      if (batchWriteResponse) {
          return response(200, batchWriteResponse)
      } else {
          throw new Error('Error at writing the results')
      }
    } catch (err) {
        console.error('Error: ', err.message)
        throw new Error(err.message)
    }

}

const buildQuestionKey = (groupIndex, questionIndex) => {
  return GROUPS_ID + ( groupIndex + 1 ) + QUESTIONS_ID + ( questionIndex + 1 )
} 

module.exports = {
    dynamoSaver
}