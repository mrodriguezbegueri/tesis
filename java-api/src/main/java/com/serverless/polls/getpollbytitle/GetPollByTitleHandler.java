package com.serverless.polls.getpollbytitle;

import java.util.Collections;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.ApiGatewayResponse;
import com.serverless.models.Poll;
import com.serverless.utils.DynamoDBPolls;

import org.apache.log4j.Logger;

public class GetPollByTitleHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger log = Logger.getLogger(GetPollByTitleHandler.class);    

    private static final DynamoDBPolls dynamoPolls = DynamoDBPolls.instance();

    @Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        
        try {

            Map<String,String> queryStringParameters = (Map<String,String>)input.get("queryStringParameters");
            String title = queryStringParameters.get("title");

            Poll poll = dynamoPolls.getPollByTitle(title);

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
            log.info("Error: " + e.toString());
    			return ApiGatewayResponse.builder()
    					.setStatusCode(500)
    					.setObjectBody("Error getting the Poll")
    					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
    					.build();
        }
    }
}
