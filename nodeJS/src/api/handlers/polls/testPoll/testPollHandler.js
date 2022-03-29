const response = (statusCode, message) => {
    return {
        statusCode: statusCode,
        body: JSON.stringify(message)
    }
}

const testPoll = async () => {
    await sleep(2200)

    return response(200, 'Test ok')
}

let sleep = (ms) => {
    return new Promise((resolve) => {
      setTimeout(resolve, ms);
    });
  }

module.exports = {
    testPoll
}