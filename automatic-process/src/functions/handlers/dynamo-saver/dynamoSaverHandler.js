const AWS = require('aws-sdk');
const db = new AWS.DynamoDB.DocumentClient({});
const { v4: uuidv4 } = require('uuid');

const tableName = process.env.POLLS_TABLE_NAME;
const dynamoResultsId = process.env.RESULTS_ID;
const dynamoAnswersId = process.env.ANSWER_ID



const response = (statusCode, message) => {
    return {
        statusCode: statusCode,
        body: JSON.stringify(message)
    }
}

const dynamoSaver = async (event) => {

    try {
        console.info(JSON.stringify(event))

        const messages = event.Records
        const result = messages[0].body

        const res = await saveResult(result)

        return res
    } catch(err) {
        console.error('error: ', err)
        throw new Error(err.message)
    }
}

const saveResult = async (itemString) => {
    const item = JSON.parse(itemString);
    const pollId = item.PK;
    const resultGroups = item.groups;
  
    // Get answers
    let answers = [];
  
    resultGroups.forEach(group => {
        let questions = group.questions;
  
        questions.forEach(question => {
          let answerId = uuidv4();
  
          let answer = {
            PutRequest: {
              Item: {
                PK: pollId,
                SK: dynamoAnswersId + '#' + answerId,
                type: question.type,
                label: question.label,
                value: question.value
              }
            }
          };
            answers.push({...answer});
        });
    });
  
    const resultId = uuidv4();
  
    const result = {
      PK: pollId,
      SK: dynamoResultsId + '#' + resultId,
      title: item.title,
      description: item.description,
      groups: item.groups,
      createdAt: new Date().toISOString()
    };
  
    // Save poll
    try {
      let res = await db.put({TableName: tableName, Item: result}).promise()
  
      if (res) {
        // Save answers
          const params = {
              RequestItems: {
                [tableName]: answers
              }
          }
          res = await db.batchWrite(params).promise()
          return response(200, res)
      } else {
          throw new Error('Error at creating the poll')
      }
    } catch (err) {
        console.error('Error: ', err.message)
        throw new Error(err.message)
    }

}


module.exports = {
    dynamoSaver
}