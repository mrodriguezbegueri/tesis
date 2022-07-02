'use strict'

const AWS = require('aws-sdk')
const axios = require('axios').default

const APIG_URL = process.env.APIG_URL

const lambda = new AWS.Lambda({
    region: process.env.REGION //change to your region
  });

const response = (statusCode, message) => {
    return {
        statusCode: statusCode,
        body: JSON.stringify(message)
    };
}

const run = async (event) => {

    try {
        const pollTitle = event.queryStringParameters.title
        console.log('pollTitle', pollTitle)

        const poll = await getPollByTitle(pollTitle)
        console.log('poll', JSON.stringify(poll))
            
        let answer = buildRandomAnswer(poll)
        console.log('answer: ', JSON.stringify(answer))

        await saveAnswer(answer)
        
        return response(200, answer)

    } catch(err) {
        console.log('err', err)
        return response(err.statusCode, err.message)
    }
}

const getPollByTitle = async (pollTitle) => {
    const params = {
        title: pollTitle
    }

    const getPollRequest = await axios.get(APIG_URL + '/polls/getPollByTitle', { params })
    const poll = getPollRequest.data

    return poll
}

const buildRandomAnswer = (poll) => {
    let answer = {...poll}

    answer.groups.forEach(group => {
        group.questions.forEach(question => {
            addRandomAnswer(question)
        })
    })

    return answer
}

const saveAnswer = async (answer) => {
    const saveAnswerRequest = await axios.post(APIG_URL + '/results', answer)
    console.log('saveAnswerRequest', saveAnswerRequest)
}

const addRandomAnswer = (question) => {
    const contOptions = question.options.length
    const randomIndex = getRandomInt(0, (contOptions - 1))
    const answer = question.options[randomIndex].label
    question.value = answer 
}

const getRandomInt = (min, max) => {
    min = Math.ceil(min)
    max = Math.floor(max)
    return Math.floor(Math.random() * (max - min + 1)) + min
}

const callBot = async (event, context, callback) => {
    try {
        // context.callbackWaitsForEmptyEventLoop = true
        const numberOfExecutions = event.queryStringParameters.number_of_executions
        const pollTitle = event.queryStringParameters.title

            // for(let i = 0; i < numberOfExecutions; i++) {
            
        await callSaveAnswerBot(pollTitle, numberOfExecutions)
            // }
        
        return response(200, 'All requests sended')

    } catch(err) {
        console.log('err', err)
        return response(err.statusCode, err.message)
    }
}

const callSaveAnswerBot = (pollTitle, numberOfExecutions) => {
    // const params = {
    //     title: pollTitle
    // }
    
    // const runBotRequest = axios.get(APIG_URL + '/bot/run', { params })
    // .catch( (err) => {
    //     console.log('err: ', err)
    // })
    // console.log('runBotRequest', runBotRequest)

    const payload = {
        queryStringParameters: {
            title: pollTitle
        }
    }

    return new Promise((resolve, reject) => {
        for(let i = 0; i < numberOfExecutions; i++) {
            console.log('i: ', i)
            lambda.invoke({
                FunctionName: process.env.SAVE_RANDOM_ANSWER_LAMBDA,
                InvocationType: 'Event',
                Payload: JSON.stringify(payload)
            })
         }
         resolve('ok')
    })
}

module.exports = {
    run,
    callBot
}