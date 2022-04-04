'use strict'

const AWS = require('aws-sdk')
const { v4: uuidv4 } = require('uuid')
// const axios = require('axios').default

// const APIG_URL = process.env.APIG_URL

// const lambda = new AWS.Lambda({
//     region: process.env.REGION //change to your region
//   });

const { RESULTS_ID, QUESTIONS_ID, GROUPS_ID } = process.env

const generateEventResponse = (processData) => {
    return {
            poll: processData.poll,
            randomResult: processData.randomResult
    }
}

const getRandomResult = async (event) => {

    try {
        const poll = event.data.poll
            
        let randomResult = buildRandomResult(poll)
        console.log('answer: ', JSON.stringify(randomResult))
        
        const eventResponseData = generateEventResponse({
            poll,
            randomResult
        })

        console.log("eventResponseData: ", eventResponseData)
        return eventResponseData
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
        createdAt: new Date().toISOString()
      }

    poll.groups.forEach( ( group, gIndex ) => {
        group.questions.forEach( ( question, qIndex ) => {
            let key = buildQuestionKey(gIndex, qIndex)
            const randomAnswer = getRandomAnswer(question)
            result[key] = randomAnswer
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


module.exports = {
    getRandomResult
}