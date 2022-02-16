const aws = require('aws-sdk');
const sfn = new aws.StepFunctions();
const { STEP_FUNCTION_ARN } = process.env

const restart = async (event) => {
    
    try {
        const StateMachineArn = STEP_FUNCTION_ARN
        event.restart.executionCount -= 1;
        event.iterator.index = -1
        event.data.title = event.data.poll.title
        delete event.data.poll
        event = JSON.stringify(event);
    
        let params = {
            input: event,
            stateMachineArn: StateMachineArn
        };
        
        await sfn.startExecution(params).promise()
            
        return {
            event
        }
        
    } catch(err) {
        console.log('error: ', err)
        throw new Error('Error at restart step function')
    }
}


module.exports = {
    restart
}