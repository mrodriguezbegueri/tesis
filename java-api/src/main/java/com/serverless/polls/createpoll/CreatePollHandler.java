package com.serverless.polls.createpoll;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.ApiGatewayResponse;
import com.serverless.models.Poll;
import com.serverless.utils.DynamoDBPolls;

import org.apache.log4j.Logger;

public class CreatePollHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private final static String DYNAMO_POLLS_ID = System.getenv("POLLS_ID");

    private static final Logger log = Logger.getLogger(CreatePollHandler.class);

    private static final DynamoDBPolls dynamoPolls = DynamoDBPolls.instance();

    @Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode body = mapper.readTree((String) input.get("body"));
            log.info("body: " + body.toString());
            
            String json = body.toString();
            
            long startTimeInit = System.nanoTime();
            Poll poll = mapper.readValue(json, Poll.class);
            long endTimeInit = System.nanoTime();
            log.info("Duración mapper poll: " + (endTimeInit-startTimeInit)/1e6 + " ms");
            
            String PK = UUID.randomUUID().toString();
            PK = DYNAMO_POLLS_ID + '#' + PK;

            poll.setPK(PK);
            poll.setSK(PK);
            poll.setGSI1PK("POLLS");

            log.info("POLL: " + poll.toString());

            long startTime = System.nanoTime();
            log.info("startTime nano: " + startTime);
            log.info("startTime ms: " + startTime/1e6 + " ms");

            dynamoPolls.savePoll(poll);

            long endTime = System.nanoTime();
            log.info("endTime nano: " + endTime);
            log.info("endTime ms: " + endTime/1e6 + " ms");

            log.info("Duración savePoll: " + (endTime-startTime)/1e6 + " ms");
            
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
    					.setObjectBody("Error creating the Poll")
    					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
    					.build();
        }
    }
}