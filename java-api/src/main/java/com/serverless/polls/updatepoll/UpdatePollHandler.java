package com.serverless.polls.updatepoll;

import java.util.Collections;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.ApiGatewayResponse;
import com.serverless.Response;
import com.serverless.utils.DynamoDBAdapter;

public class UpdatePollHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    
    private static final String DYNAMO_POLLS_ID = System.getenv("POLLS_ID");
    private static final String POLLS_TABLE_NAME = System.getenv("POLLS_TABLE_NAME");
    private DynamoDBAdapter dbAdapter;
    private AmazonDynamoDB client;
	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        
        try {

            dbAdapter = DynamoDBAdapter.getInstance(dbAdapter);
            client = dbAdapter.getDbClient();

            DynamoDB dynamoDB = new DynamoDB(client);
            Table table = dynamoDB.getTable(POLLS_TABLE_NAME);

            Map<String,String> pathParameters = (Map<String,String>) input.get("pathParameters");
            String pollId = pathParameters.get("id");

            ObjectMapper mapper = new ObjectMapper();
            JsonNode body = mapper.readTree((String) input.get("body"));
            context.getLogger().log("body: " + body.toString());
            
            String paramName = body.get("paramName").asText();
            context.getLogger().log("paramName: " + paramName);
            String paramValue = body.get("paramValue").asText();
            context.getLogger().log("paramValue: " + paramValue);

            String PK = DYNAMO_POLLS_ID + '#' + pollId;

            UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("PK", PK, "SK", PK)
                .withUpdateExpression("set " + paramName + " = :v")
                .withValueMap(new ValueMap().withString(":v", paramValue))
                .withReturnValues(ReturnValue.UPDATED_NEW);

            context.getLogger().log("Updating Item...");
            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
            context.getLogger().log("UpdateItem succeeded:\n " + outcome.getItem().toJSONPretty());
                

            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(outcome.getItem().asMap())
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
