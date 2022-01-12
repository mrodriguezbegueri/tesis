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

    // Obtener la encuesta

    try {

        const pollTitle = event.queryStringParameters.title
        console.log('pollTitle', pollTitle)

        const getPollRequest = await axios.get(APIG_URL + '/polls/getPollByTitle', {
            params: {
                title: pollTitle
            }
        })
    
        const poll = getPollRequest.data
        console.log('poll', poll)
    
        return response(200, 'ok')

    } catch(err) {
        console.log('err', err)
    }
}

module.exports = {
    run
}