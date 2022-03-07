package com.serverless.polls.createpoll;

// import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
// import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
// import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
// import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.ApiGatewayResponse;
import com.serverless.Response;
import com.serverless.models.Poll;

// import org.apache.logging.log4j.Logger;

// import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
// import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
// import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

// import java.io.IOException;
import java.util.Collections;
import java.util.Map;
// import java.util.logging.Logger;

public class CreatePollHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {


    // private final Logger logger = Logger.getLogger(name, resourceBundleName)

    @Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        
        try {
            context.getLogger().log("Entrando al handler");
            JsonNode body = new ObjectMapper().readTree((String) input.get("body"));
 
            // create the Product object for post
            Poll poll = new Poll();
            // poll.setId(body.get("id").asText());
            poll.setPK(body.get("PK").asText());
            poll.setSK(body.get("SK").asText());
            poll.setDescription(body.get("description").asText());
            poll.setTitle(body.get("title").asText());
            poll.setGSI1PK(body.get("GSI1PK").asText());

            context.getLogger().log("Poll: " + poll.toString());
            poll.save(poll);
            context.getLogger().log("Despues del save");
    
            // send the response back
            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    // .setbo
                    // .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();
        } catch (Exception e) {
            Response responseBody = new Response("Error in saving poll: ", input);
    			return ApiGatewayResponse.builder()
    					.setStatusCode(500)
    					.setObjectBody(responseBody)
    					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
    					.build();
        }
    
    }

    

}