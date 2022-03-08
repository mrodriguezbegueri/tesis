package com.serverless.polls.createpoll;

// import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
// import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
// import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
// import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.serverless.ApiGatewayResponse;
import com.serverless.Response;
import com.serverless.models.Group;
import com.serverless.models.Poll;

import java.util.ArrayList;

// import org.apache.logging.log4j.Logger;

// import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
// import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
// import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

// import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
// import java.util.logging.Logger;

public class CreatePollHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {


    // private final Logger logger = Logger.getLogger(name, resourceBundleName)

    @Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        
        try {
            context.getLogger().log("Entrando al handler");
            ObjectMapper mapper = new ObjectMapper();
            JsonNode body = mapper.readTree((String) input.get("body"));
            context.getLogger().log("body: " + body.toString());

            String json = body.toString();

            Poll poll = mapper.readValue(json, Poll.class);
            context.getLogger().log("group description: " + poll.getGroups().get(0).getDescription());
            context.getLogger().log("group label: " + poll.getGroups().get(0).getLabel());
            
            // create the Product object for post
            // Poll poll = new Poll();

            // Gson converter = new Gson();
            // java.lang.reflect.Type type = new TypeToken<ArrayList<Group>>(){}.getType();
            // ArrayList<Group> groupsList = converter.fromJson(body.get("groups").as, type);

            // context.getLogger().log("body: " + groupsList.toString());
            // context.getLogger().log("body: " + groupsList.get(0).toString());
            
            // poll.setPK(body.get("PK").asText());
            // poll.setSK(body.get("SK").asText());
            // poll.setDescription(body.get("description").asText());
            // poll.setTitle(body.get("title").asText());
            // poll.setGSI1PK(body.get("GSI1PK").asText());
            // // poll.setGroups(body.get("groups").lis());
            // context.getLogger().log("Poll: " + poll.toString());
            // poll.save(poll);
            // context.getLogger().log("Despues del save");
    
            poll.save(poll);
            
            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(poll)
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