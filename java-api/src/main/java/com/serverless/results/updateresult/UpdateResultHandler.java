package com.serverless.results.updateresult;

import java.util.Collections;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.ApiGatewayResponse;
import com.serverless.models.Result;

public class UpdateResultHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    
    private static final String DYNAMO_POLLS_ID = System.getenv("POLLS_ID");
    private static final String DYNAMO_RESULTS_ID = System.getenv("RESULTS_ID");
	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        
        try {

            Map<String,String> pathParameters = (Map<String,String>) input.get("pathParameters");
            String resultId = pathParameters.get("id");
            Map<String,String> queryStringParameters =  (Map<String,String>)input.get("queryStringParameters");
            String pollId = queryStringParameters.get("pollId");

            ObjectMapper mapper = new ObjectMapper();
            JsonNode body = mapper.readTree((String) input.get("body"));
            context.getLogger().log("body: " + body.toString());

            String jsonResult = body.toString();

            Result result = mapper.readValue(jsonResult, Result.class);

            String SK = DYNAMO_POLLS_ID + '#' + pollId; 
            String PK = DYNAMO_RESULTS_ID + '#' + resultId; 
            result.setPK(PK);
            result.setSK(SK);

            result.updateResult(result);
                
            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(result)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();

        } catch (Exception e) {
            context.getLogger().log("Error: " + e.toString());
    		return ApiGatewayResponse.builder()
    				.setStatusCode(500)
    				.setObjectBody("Error updating the Result")
    				.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
    				.build();
        }
    }
}
