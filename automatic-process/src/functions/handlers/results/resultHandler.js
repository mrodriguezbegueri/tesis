'use strict'

const AWS = require('aws-sdk')
// const axios = require('axios').default

// const APIG_URL = process.env.APIG_URL

// const lambda = new AWS.Lambda({
//     region: process.env.REGION //change to your region
//   });

const generateEventResponse = (processData) => {
    return {
            poll: processData.poll,
            randomResult: processData.randomResult
    }
}

const getRandomResult = async (event) => {

    try {
        const poll = event.data.poll
            
        let randomResult = buildRandomAnswer(poll)
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

const buildRandomAnswer = (poll) => {
    let answer = JSON.parse(JSON.stringify(poll))

    answer.groups.forEach(group => {
        group.questions.forEach(question => {
            addRandomAnswer(question)
        })
    })

    return answer
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
    getRandomResult
}