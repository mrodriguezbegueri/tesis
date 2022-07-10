package com.serverless.results.deleteresult;

import java.util.Collections;
import java.util.Map;

import org.apache.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.ApiGatewayResponse;
import com.serverless.models.Result;
import com.serverless.utils.DynamoDBResults;

public class DeleteResultHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private final static String DYNAMO_FORMS_ID = System.getenv("FORMS_ID");
    private final static String DYNAMO_RESULTS_ID = System.getenv("RESULTS_ID");

    private static final Logger log = Logger.getLogger(DeleteResultHandler.class);

    private static final DynamoDBResults dynamoResults = DynamoDBResults.instance();

    @Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        
        try {

            Map<String,String> pathParameters =  (Map<String,String>)input.get("pathParameters");
            Map<String,String> queryStringParameters =  (Map<String,String>)input.get("queryStringParameters");
            String id = pathParameters.get("id");
            String PK = DYNAMO_RESULTS_ID + '#' + id;

            String formId = queryStringParameters.get("formId");
            String SK = DYNAMO_FORMS_ID + '#' + formId;

            Result result = new Result();
            result.setPK(PK);
            result.setSK(SK);

            // result.deleteResult(result);
            dynamoResults.deleteResult(result);

            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody("Result with id: " + PK + " deleted successfully.")
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();

        } catch (Exception e) {
            log.info("Error: " + e.toString());
    		return ApiGatewayResponse.builder()
    				.setStatusCode(500)
    				.setObjectBody("Error deleting the Result")
    				.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
    				.build();
        }
    }
}
