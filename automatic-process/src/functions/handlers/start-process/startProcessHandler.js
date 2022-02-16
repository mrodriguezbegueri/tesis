const AWS = require('aws-sdk')
const stepFunctionsClient = new AWS.StepFunctions();
const STEP_FUNCTION_ARN = process.env.STEP_FUNCTION_ARN

const response = (statusCode, message) => {
    return {
        statusCode: statusCode,
        body: JSON.stringify(message)
    }
}

const startProcess = async (event) => {
    try {
        const reqBody = JSON.parse(event.body);
        // const title = reqBody.title
        // const recordsToSent = reqBody.recordsToSent
        const { count, index, step, title, executionCount } = reqBody
        

        const step_function_input = {
            restart: {
                executionCount
            },
            iterator: {
                count,
                index,
                step
            },
            data: {
                title
            }
        }

        const step_function_params = {
            stateMachineArn: STEP_FUNCTION_ARN,
            input: JSON.stringify(step_function_input),
            // name: 'STRING_VALUE',
            // traceHeader: 'STRING_VALUE'
        }

        console.log("step params:", step_function_params)
        const stepExecution = await stepFunctionsClient.startExecution(step_function_params).promise()
        
        return response(200, stepExecution)

    } catch(err) {
        console.log(err)
        response(500 ,err)
    }
}


module.exports = {
    startProcess
}