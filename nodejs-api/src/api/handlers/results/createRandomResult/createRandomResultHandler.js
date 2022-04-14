'use strict'

const AWS = require('aws-sdk')
const { v4: uuidv4 } = require('uuid')
// const db = new AWS.DynamoDB.DocumentClient({});

const { RESULTS_ID, QUESTIONS_ID, GROUPS_ID, POLLS_TABLE_NAME } = process.env

const response = (statusCode, message) => {
    return {
        statusCode: statusCode,
        body: JSON.stringify(message)
    }
}

const createRandomResult = async (event) => {

    try {
        const poll = JSON.parse(event.body)
            
        let randomResult = buildRandomResult(poll)
        console.log('answer: ', JSON.stringify(randomResult))

        // const res = await saveResult(randomResult)

        return response(200, randomResult)
        
    } catch(err) {
        console.log('err', err)
    }
}

const buildRandomResult = (poll) => {
    
    const resultId = uuidv4()

    let result = {
        PK: RESULTS_ID + '#' + resultId,
        SK: poll.PK,
        title: poll.title,
        description: poll.description,
        groups: poll.groups,
        // createdAt: new Date().toISOString()
      }

    poll.groups.forEach( ( group, gIndex ) => {
        group.questions.forEach( ( question, qIndex ) => {
            // let key = buildQuestionKey(gIndex, qIndex)
            const randomAnswer = getRandomAnswer(question)
            question['value'] = randomAnswer
            // result[key] = randomAnswer
        })
    })
    return result
}

const buildQuestionKey = (groupIndex, questionIndex) => {
    return GROUPS_ID + ( groupIndex + 1 ) + QUESTIONS_ID + ( questionIndex + 1 )
} 

const getRandomAnswer = (question) => {

    let answer = null

    if (question.type === 'number') {
        const randomNumber = getRandomInt(1, 100)
        answer = randomNumber
    } else {
        const contOptions = question.options.length
        const randomIndex = getRandomInt(0, (contOptions - 1))
        answer = question.options[randomIndex].label
    }

    if (!answer) {
        throw new Error('Error with random answer')
    }

    return answer
}

const getRandomInt = (min, max) => {
    min = Math.ceil(min)
    max = Math.floor(max)
    return Math.floor(Math.random() * (max - min + 1)) + min
}

// const saveResult = async (result) => {
//     try {

//         const params = {
//             TableName: POLLS_TABLE_NAME,
//             Item: result
//         }
        
//         let res = await db.put(params).promise()
    
//         if (res) {
//             return res
//         } else {
//             throw new Error('Error at creating the result')
//         }
//       } catch (err) {
//           console.error('Error: ', err.message)
//           throw new Error(err.message)
//       }
// }


module.exports = {
    createRandomResult
}