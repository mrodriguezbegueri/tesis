package com.serverless.results.createresult;

import java.util.Collections;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.ApiGatewayResponse;
import com.serverless.models.Result;

public class CreateResultHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {


    @Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        
        try {

            ObjectMapper mapper = new ObjectMapper();
            JsonNode body = mapper.readTree((String) input.get("body"));
            context.getLogger().log("body: " + body.toString());

            String json = body.toString();
            
            Result result = mapper.readValue(json, Result.class);

            result.save(result);
            
            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(result)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();

        } catch (Exception e) {
            context.getLogger().log("Error: " + e.toString());
    			return ApiGatewayResponse.builder()
    					.setStatusCode(500)
    					.setObjectBody("Error creating the Result")
    					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
    					.build();
        }
    }
}