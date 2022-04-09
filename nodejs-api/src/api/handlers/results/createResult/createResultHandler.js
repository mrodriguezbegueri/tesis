'use strict';

const AWS = require('aws-sdk')
const db = new AWS.DynamoDB.DocumentClient({})
const { v4: uuidv4 } = require('uuid')

const { POLLS_TABLE_NAME, RESULTS_ID, QUESTIONS_ID, GROUPS_ID } = process.env

const response = (statusCode, message) => {
    return {
        statusCode: statusCode,
        body: JSON.stringify(message)
    }
}

const createResult = async (event) => {
  try {
  
    const reqBody = JSON.parse(event.body)
    const pollId = reqBody.PK
    const resultGroups = reqBody.groups

    const resultId = uuidv4()

    let result = {
      PK: RESULTS_ID + '#' + resultId,
      SK: pollId,
      title: reqBody.title,
      description: reqBody.description,
      groups: reqBody.groups,
      createdAt: new Date().toISOString()
    }


    resultGroups.forEach( (group, gIndex) => {
        let questions = group.questions

        questions.forEach( (question, qIndex) => {
          let key = buildQuestionKey(gIndex, qIndex)
          console.info('key: ', key)
          result[key] = question.value
        })
    })

    console.log(JSON.stringify(result))

  
    // Save poll
    let res = await db.put({TableName: POLLS_TABLE_NAME, Item: result}).promise()

    if (res) {
        return response(200, res)
    } else {
        return response(500, { message: 'Error at creating the poll' })
    }
  } catch (err) {
    console.error('err: ', err)
    return response(err.statusCode, err)
  }
}

const buildQuestionKey = (groupIndex, questionIndex) => {
  return GROUPS_ID + ( groupIndex + 1 ) + QUESTIONS_ID + ( questionIndex + 1 )
} 


module.exports = {
  createResult
}