const axios = require('axios').default
// const API_URL = 'https://e1ib5jr340.execute-api.us-east-1.amazonaws.com/tesis/polls/badec2fc-4476-4cfe-82ba-33c6351323af' // PYTHON
// const API_URL = 'https://w8u7sjpwec.execute-api.us-east-1.amazonaws.com/tesis/polls/badec2fc-4476-4cfe-82ba-33c6351323af' // NODEJS
const API_URL = 'https://b560n6l4r0.execute-api.us-east-1.amazonaws.com/tesis/polls/badec2fc-4476-4cfe-82ba-33c6351323af' // JAVA


const params = {
    
}

const loopRequests = async (N) => {

    let requests = []

    for(let i = 0; i < N; i++) {
        let request = axios.get(API_URL, { params })
        requests.push(request)
    }

    let response = await Promise.all(requests)

    return response
}

const main = async () => {

    const call = await loopRequests(1000)
    let succeedRequests = 0
    call.forEach( (req) => {
        if (req.status === 200) {
            succeedRequests++
        }
    } )

    console.log('Succeed Requests: ', succeedRequests)
}

main()