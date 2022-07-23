'use strict'

const { v4: uuidv4 } = require('uuid')

const { RESULTS_ID } = process.env

const response = (statusCode, message) => {
    return {
        statusCode: statusCode,
        body: JSON.stringify(message)
    }
}

const createRandomResult = async (event) => {
    try {
        const form = JSON.parse(event.body)
        console.log('body: ', JSON.stringify(event.body))
        let randomResult = buildRandomResult(form)
        console.log('answer: ', JSON.stringify(randomResult))

        return response(200, randomResult)

    } catch (err) {
        console.log('err', err)
    }
}

const buildRandomResult = (form) => {

    const resultId = uuidv4()

    let result = {
        PK: RESULTS_ID + '#' + resultId,
        SK: form.PK,
        title: form.title,
        description: form.description,
        groups: form.groups,
    }

    form.groups.forEach((group) => {
        group.questions.forEach((question) => {
            const randomAnswer = getRandomAnswer(question)
            question['value'] = randomAnswer
        })
    })
    return result
}


const getRandomAnswer = (question) => {

    let answer = null

    if (question.type === 'number') {
        const randomNumber = getRandomInt(1, 100)
        answer = randomNumber
    } else {
        const contOptions = question.options.length
        const randomIndex = getRandomInt(0, (contOptions - 1))
        answer = question.options[randomIndex].label
    }

    if (!answer) {
        throw new Error('Error with random answer')
    }

    return answer
}

const getRandomInt = (min, max) => {
    min = Math.ceil(min)
    max = Math.floor(max)
    return Math.floor(Math.random() * (max - min + 1)) + min
}

// const buildQuestionKey = (groupIndex, questionIndex) => {
//     return GROUPS_ID + ( groupIndex + 1 ) + QUESTIONS_ID + ( questionIndex + 1 )
// } 
// const saveResult = async (result) => {
//     try {

//         const params = {
//             TableName: FORMS_TABLE_NAME,
//             Item: result
//         }

//         let res = await db.put(params).promise()

//         if (res) {
//             return res
//         } else {
//             throw new Error('Error at creating the result')
//         }
//       } catch (err) {
//           console.error('Error: ', err.message)
//           throw new Error(err.message)
//       }
// }


module.exports = {
    createRandomResult
}