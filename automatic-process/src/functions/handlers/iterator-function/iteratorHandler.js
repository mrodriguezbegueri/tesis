'use strict'

// const generateEventResponse = (processData) => {
//     return {
//         data: {
//             poll: processData.poll,
//             recordsSent: processData.recordsSent,
//             recordsToSent: processData.recordsToSent
//         }
//     }
// }

const iterator = async (event) => {

    let index = event.iterator.index
    let step = event.iterator.step
    let count = event.iterator.count
 
    index += step

    const shouldContinue = index < count

    return {
        index,
        step,
        count,
        shouldContinue
    }
}

module.exports = {
    iterator
}