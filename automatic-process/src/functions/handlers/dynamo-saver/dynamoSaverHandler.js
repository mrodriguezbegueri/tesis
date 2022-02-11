
const response = (statusCode, message) => {
    return {
        statusCode: statusCode,
        body: JSON.stringify(message)
    }
}

const dynamoSaver = async (event) => {

    console.info(JSON.stringify(event))

    return response(200, 'ok')
    // throw new Error('error')
}


module.exports = {
    dynamoSaver
}