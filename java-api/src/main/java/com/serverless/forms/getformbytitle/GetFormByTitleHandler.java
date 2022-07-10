package com.serverless.forms.getformbytitle;

import java.util.Collections;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.ApiGatewayResponse;
import com.serverless.models.Form;
import com.serverless.utils.DynamoDBForms;

import org.apache.log4j.Logger;

public class GetFormByTitleHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger log = Logger.getLogger(GetFormByTitleHandler.class);    

    private static final DynamoDBForms dynamoForms = DynamoDBForms.instance();

    @Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        
        try {

            Map<String,String> queryStringParameters = (Map<String,String>)input.get("queryStringParameters");
            String title = queryStringParameters.get("title");

            Form form = dynamoForms.getFormByTitle(title);

            if (form == null) {
                return ApiGatewayResponse.builder()
                    .setStatusCode(404)
                    .setObjectBody("Form with title: " + title + " not found.")
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();
            }

            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(form)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();

        } catch (Exception e) {
            log.info("Error: " + e.toString());
    			return ApiGatewayResponse.builder()
    					.setStatusCode(500)
    					.setObjectBody("Error getting the Form")
    					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
    					.build();
        }
    }
}
