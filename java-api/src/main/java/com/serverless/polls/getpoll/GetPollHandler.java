package com.serverless.polls.getpoll;

import java.util.Collections;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.ApiGatewayResponse;
import com.serverless.Response;
import com.serverless.models.Poll;

public class GetPollHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private final static String DYNAMO_POLLS_ID = System.getenv("POLLS_ID");

    @Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        
        try {

            Map<String,String> pathParameters =  (Map<String,String>)input.get("pathParameters");
            String id = pathParameters.get("id");
            String pollId = DYNAMO_POLLS_ID + '#' + id;

            Poll poll = new Poll().getPoll(pollId);

            if (poll == null) {
                return ApiGatewayResponse.builder()
                    .setStatusCode(404)
                    .setObjectBody("Poll with id: '" + pollId + "' not found.")
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();
            }

            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(poll)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();

        } catch (Exception e) {
            Response responseBody = new Response(e.toString(), input);
    			return ApiGatewayResponse.builder()
    					.setStatusCode(500)
    					.setObjectBody(responseBody)
    					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
    					.build();
        }
    }
}
