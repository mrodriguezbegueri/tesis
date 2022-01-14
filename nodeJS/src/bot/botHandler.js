'use strict'

const AWS = require('aws-sdk')
const axios = require('axios').default

const APIG_URL = process.env.APIG_URL

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
        console.log('poll', poll)
        
        buildRandomAnswer(poll)
        console.log('poll after: ', JSON.stringify(poll))

        await saveAnswer(poll)
        
        return response(200, poll)

    } catch(err) {
        console.log('err', err)
        return response(err.statusCode, err.message)
    }
}

const buildRandomAnswer = (poll) => {
    poll.groups.forEach(group => {
        group.questions.forEach(question => {
            addRandomAnswer(question)
        })
    })
}

const getPollByTitle = async (pollTitle) => {
    const params = {
        title: pollTitle
    }

    const getPollRequest = await axios.get(APIG_URL + '/polls/getPollByTitle', { params })
    const poll = getPollRequest.data

    return poll
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

const saveAnswer = async (answer) => {
    const saveAnswerRequest = await axios.post(APIG_URL + '/results', answer)
    console.log('saveAnswerRequest', saveAnswerRequest)
}

module.exports = {
    run
}