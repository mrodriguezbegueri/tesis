package com.serverless.polls.createpoll;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.ApiGatewayResponse;
import com.serverless.Response;
import com.serverless.models.Poll;

public class CreatePollHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private final static String DYNAMO_POLLS_ID = System.getenv("POLLS_ID");

    @Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        
        try {

            ObjectMapper mapper = new ObjectMapper();
            JsonNode body = mapper.readTree((String) input.get("body"));
            context.getLogger().log("body: " + body.toString());

            String json = body.toString();
            
            Poll poll = mapper.readValue(json, Poll.class);
            
            String PK = UUID.randomUUID().toString();
            PK = DYNAMO_POLLS_ID + '#' + PK;

            poll.setPK(PK);
            poll.setSK(PK);
            poll.setGSI1PK("POLLS");

            context.getLogger().log("POLL: " + poll.toString());

            poll.save(poll);
            
            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(poll)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();

        } catch (Exception e) {
            context.getLogger().log("Error: " + e.toString());
            // Response responseBody = new Response(e.toString(), input);
    			return ApiGatewayResponse.builder()
    					.setStatusCode(500)
    					.setObjectBody("Error creating the Poll")
    					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
    					.build();
        }
    }
}