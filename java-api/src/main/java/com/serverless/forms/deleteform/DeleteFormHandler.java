package com.serverless.forms.deleteform;

import java.util.Collections;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.ApiGatewayResponse;
import com.serverless.models.Form;
import com.serverless.utils.DynamoDBForms;

import org.apache.log4j.Logger;

public class DeleteFormHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private final static String DYNAMO_FORMS_ID = System.getenv("FORMS_ID");

    private static final Logger log = Logger.getLogger(DeleteFormHandler.class);

    private static final DynamoDBForms dynamoForms = DynamoDBForms.instance();

    @Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        
        try {

            Map<String,String> pathParameters =  (Map<String,String>)input.get("pathParameters");
            String id = pathParameters.get("id");
            String PK = DYNAMO_FORMS_ID + '#' + id;

            Form form = new Form();
            form.setPK(PK);
            form.setSK(PK);

            dynamoForms.deleteForm(form);

            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody("Form with id: " + PK + " deleted successfully.")
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();

        } catch (Exception e) {
            log.info("Error: " + e.toString());
            // Response responseBody = new Response(e.toString(), input);
    		return ApiGatewayResponse.builder()
    				.setStatusCode(500)
    				.setObjectBody("Error deleting the Form")
    				.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
    				.build();
        }
    }
}
