package com.serverless.forms.updateform;

import java.util.Collections;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.ApiGatewayResponse;
import com.serverless.models.Form;
import com.serverless.utils.DynamoDBForms;

import org.apache.log4j.Logger;

public class UpdateFormHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    
    private static final String DYNAMO_FORMS_ID = System.getenv("FORMS_ID");
    
    private static final Logger log = Logger.getLogger(UpdateFormHandler.class);

    private static final DynamoDBForms dynamoForms = DynamoDBForms.instance();
	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        
        try {
            log.info("Update Form Lambda");
            Map<String,String> pathParameters = (Map<String,String>) input.get("pathParameters");
            String formId = pathParameters.get("id");

            ObjectMapper mapper = new ObjectMapper();
            JsonNode body = mapper.readTree((String) input.get("body"));
            context.getLogger().log("body: " + body.toString());

            String jsonForm= body.toString();

            Form form = mapper.readValue(jsonForm, Form.class);

            String PK = DYNAMO_FORMS_ID + '#' + formId;
            form.setPK(PK);
            form.setSK(PK);

            dynamoForms.updateForm(form);
                
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
    				.setObjectBody("Error updating the Form")
    				.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
    				.build();
        }
    }
}
