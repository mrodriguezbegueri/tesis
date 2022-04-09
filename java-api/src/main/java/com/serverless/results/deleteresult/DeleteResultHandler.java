package com.serverless.results.deleteresult;

import java.util.Collections;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.ApiGatewayResponse;
import com.serverless.models.Result;

public class DeleteResultHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private final static String DYNAMO_POLLS_ID = System.getenv("POLLS_ID");
    private final static String DYNAMO_RESULTS_ID = System.getenv("RESULTS_ID");

    @Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        
        try {

            Map<String,String> pathParameters =  (Map<String,String>)input.get("pathParameters");
            Map<String,String> queryStringParameters =  (Map<String,String>)input.get("queryStringParameters");
            String id = pathParameters.get("id");
            String PK = DYNAMO_RESULTS_ID + '#' + id;

            String pollId = queryStringParameters.get("pollId");
            String SK = DYNAMO_POLLS_ID + '#' + pollId;

            Result result = new Result();
            result.setPK(PK);
            result.setSK(SK);

            result.deleteResult(result);

            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody("Result with id: " + PK + " deleted successfully.")
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();

        } catch (Exception e) {
            context.getLogger().log("Error: " + e.toString());
    		return ApiGatewayResponse.builder()
    				.setStatusCode(500)
    				.setObjectBody("Error deleting the Result")
    				.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
    				.build();
        }
    }
}
