package com.serverless.polls.deletepoll;

import java.util.Collections;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.ApiGatewayResponse;
import com.serverless.Response;
import com.serverless.models.Poll;

public class DeletePollHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private final static String DYNAMO_POLLS_ID = System.getenv("POLLS_ID");

    @Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        
        try {

            Map<String,String> pathParameters =  (Map<String,String>)input.get("pathParameters");
            String id = pathParameters.get("id");
            String PK = DYNAMO_POLLS_ID + '#' + id;

            Poll poll = new Poll();
            poll.setPK(PK);
            poll.setSK(PK);

            poll.deletePoll(poll);

            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody("Poll with id: " + PK + " deleted successfully.")
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();

        } catch (Exception e) {
            context.getLogger().log("Error: " + e.toString());
            // Response responseBody = new Response(e.toString(), input);
    		return ApiGatewayResponse.builder()
    				.setStatusCode(500)
    				.setObjectBody("Error deleting the Poll")
    				.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
    				.build();
        }
    }
}
