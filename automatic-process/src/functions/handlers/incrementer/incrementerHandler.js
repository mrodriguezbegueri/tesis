'use strict'

const generateEventResponse = (processData) => {
    return {
        data: {
            poll: processData.poll,
            recordsSent: processData.recordsSent,
            recordsToSent: processData.recordsToSent
        }
    }
}

const incrementer = async (event) => {

    let recordsSent = event.data.recordsSent
    const recordsToSent = event.data.recordsToSent
    const poll = event.data.poll

    recordsSent++

    return generateEventResponse({
        poll,
        recordsSent,
        recordsToSent
    })
}

module.exports = {
    incrementer
}