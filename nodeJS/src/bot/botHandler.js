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
        // Obtener la encuesta        
        const pollTitle = event.queryStringParameters.title
        console.log('pollTitle', pollTitle)

        const params = {
            title: pollTitle
        }

        const getPollRequest = await axios.get(APIG_URL + '/polls/getPollByTitle', { params })
    
        const poll = getPollRequest.data
        console.log('poll', poll)

        // Armar las respuestas
        getPollResult(poll)
        console.log('poll after: ', JSON.stringify(poll))
        
        
        return response(200, 'ok')

    } catch(err) {
        console.log('err', err)
    }
}

const getPollResult = (poll) => {
    poll.groups.forEach(group => {
        group.questions.forEach(question => {
            addRandomAnswer(question)
        })
    })
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

module.exports = {
    run
}