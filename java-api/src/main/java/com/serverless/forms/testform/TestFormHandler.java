package com.serverless.forms.testform;

import java.util.Collections;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.ApiGatewayResponse;

public class TestFormHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    @Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        
        try {

            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody("Test form succeed!!")
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();

        } catch (Exception e) {
            context.getLogger().log("Error: " + e.toString());
    			return ApiGatewayResponse.builder()
    					.setStatusCode(500)
    					.setObjectBody("Error")
    					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
    					.build();
        }
    }
}
