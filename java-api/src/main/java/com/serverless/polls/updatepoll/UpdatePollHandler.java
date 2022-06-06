package com.serverless.polls.updatepoll;

import java.util.Collections;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.ApiGatewayResponse;
import com.serverless.models.Poll;
import com.serverless.utils.DynamoDBPolls;

import org.apache.log4j.Logger;

public class UpdatePollHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    
    private static final String DYNAMO_POLLS_ID = System.getenv("POLLS_ID");
    
    private static final Logger log = Logger.getLogger(UpdatePollHandler.class);

    private static final DynamoDBPolls dynamoPolls = DynamoDBPolls.instance();
	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        
        try {
            log.info("Update Poll Lambda");
            Map<String,String> pathParameters = (Map<String,String>) input.get("pathParameters");
            String pollId = pathParameters.get("id");

            ObjectMapper mapper = new ObjectMapper();
            JsonNode body = mapper.readTree((String) input.get("body"));
            context.getLogger().log("body: " + body.toString());

            String jsonPoll = body.toString();

            Poll poll = mapper.readValue(jsonPoll, Poll.class);

            String PK = DYNAMO_POLLS_ID + '#' + pollId;
            poll.setPK(PK);
            poll.setSK(PK);

            dynamoPolls.updatePoll(poll);
                
            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(poll)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();

        } catch (Exception e) {
            log.info("Error: " + e.toString());
            // Response responseBody = new Response(e.toString(), input);
    		return ApiGatewayResponse.builder()
    				.setStatusCode(500)
    				.setObjectBody("Error updating the Poll")
    				.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
    				.build();
        }
    }
}
