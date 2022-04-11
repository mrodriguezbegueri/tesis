package com.serverless.polls.getpollbytitle;

import java.util.Collections;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.ApiGatewayResponse;
import com.serverless.models.Poll;

public class GetPollByTitleHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    @Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        
        try {

            Map<String,String> queryStringParameters =  (Map<String,String>)input.get("queryStringParameters");
            String title = queryStringParameters.get("title");

            Poll poll = new Poll().getPollByTitle(title);

            if (poll == null) {
                return ApiGatewayResponse.builder()
                    .setStatusCode(404)
                    .setObjectBody("Poll with title: " + title + " not found.")
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();
            }

            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(poll)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();

        } catch (Exception e) {
            context.getLogger().log("Error: " + e.toString());
    			return ApiGatewayResponse.builder()
    					.setStatusCode(500)
    					.setObjectBody("Error getting the Poll")
    					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
    					.build();
        }
    }
}
