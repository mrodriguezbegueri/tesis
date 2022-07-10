package com.serverless.forms.createform;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.ApiGatewayResponse;
import com.serverless.models.Form;
import com.serverless.utils.DynamoDBForms;

import org.apache.log4j.Logger;

public class CreateFormHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private final static String DYNAMO_FORMS_ID = System.getenv("FORMS_ID");

    private static final Logger log = Logger.getLogger(CreateFormHandler.class);

    private static final DynamoDBForms dynamoForms = DynamoDBForms.instance();

    @Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode body = mapper.readTree((String) input.get("body"));
            log.info("body: " + body.toString());
            
            String json = body.toString();
            
            long startTimeInit = System.nanoTime();
            Form form = mapper.readValue(json, Form.class);
            long endTimeInit = System.nanoTime();
            log.info("Duración mapper form: " + (endTimeInit-startTimeInit)/1e6 + " ms");
            
            String PK = UUID.randomUUID().toString();
            PK = DYNAMO_FORMS_ID + '#' + PK;

            form.setPK(PK);
            form.setSK(PK);
            form.setGSI1PK("FORMS");

            log.info("FORM: " + form.toString());

            long startTime = System.nanoTime();
            log.info("startTime nano: " + startTime);
            log.info("startTime ms: " + startTime/1e6 + " ms");

            dynamoForms.saveForm(form);

            long endTime = System.nanoTime();
            log.info("endTime nano: " + endTime);
            log.info("endTime ms: " + endTime/1e6 + " ms");

            log.info("Duración saveForm: " + (endTime-startTime)/1e6 + " ms");
            
            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(form)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();

        } catch (Exception e) {
            log.info("Error: " + e.toString());
            // Response responseBody = new Response(e.toString(), input);
    			return ApiGatewayResponse.builder()
    					.setStatusCode(500)
    					.setObjectBody("Error creating the Form")
    					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
    					.build();
        }
    }
}