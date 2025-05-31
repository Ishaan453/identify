package com.bitespeed.handler;

import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

public class testHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        // Instantiate the handler
        IdentityHandler handler = new IdentityHandler();

        // Test Case 1: Existing contact (should link to primary with ID 2)
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        String requestBody = "{\"email\":\"mcfly@hillvalley.edu\",\"phoneNumber\":\"123456\"}";
        requestEvent.setBody(requestBody);

        // Simulate Lambda context
        Context context = new Context() {
            @Override
            public String getAwsRequestId() { return "test-request-id"; }
            @Override
            public String getLogGroupName() { return "test-log-group"; }
            @Override
            public String getLogStreamName() { return "test-log-stream"; }
            @Override
            public String getFunctionName() { return "test-function"; }
            @Override
            public String getFunctionVersion() { return "test-version"; }
            @Override
            public String getInvokedFunctionArn() { return "test-arn"; }
            @Override
            public com.amazonaws.services.lambda.runtime.LambdaLogger getLogger() {
                return new com.amazonaws.services.lambda.runtime.LambdaLogger() {
                    @Override
                    public void log(String string) { System.out.println(string); }

                    @Override
                    public void log(byte[] message) {
                        System.out.println(message);
                    }
                };
            }
            @Override
            public int getMemoryLimitInMB() { return 128; }
            @Override
            public int getRemainingTimeInMillis() { return 30000; }
            @Override
            public com.amazonaws.services.lambda.runtime.ClientContext getClientContext() { return null; }
            @Override
            public CognitoIdentity getIdentity() { return null; }
        };

        // Invoke the handler
        APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);
        System.out.println("Test Case 1 - Existing Contact:");
        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody());
        System.out.println();

        // Test Case 2: Merging two primary contacts (IDs 4 and 5)
        requestEvent = new APIGatewayProxyRequestEvent();
        requestBody = "{\"email\":\"george@hillvalley.edu\",\"phoneNumber\":\"717171\"}";
        requestEvent.setBody(requestBody);
        response = handler.handleRequest(requestEvent, context);
        System.out.println("Test Case 2 - Merging Two Primary Contacts:");
        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody());
        System.out.println();

        // Test Case 3: New contact (no matches)
        requestEvent = new APIGatewayProxyRequestEvent();
        requestBody = "{\"email\":\"new@fluxkart.com\",\"phoneNumber\":\"new123\"}";
        requestEvent.setBody(requestBody);
        response = handler.handleRequest(requestEvent, context);
        System.out.println("Test Case 3 - New Contact:");
        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody());
        System.out.println();

        // Test Case 4: Invalid request (missing both email and phone)
        requestEvent = new APIGatewayProxyRequestEvent();
        requestBody = "{\"email\":null,\"phoneNumber\":null}";
        requestEvent.setBody(requestBody);
        response = handler.handleRequest(requestEvent, context);
        System.out.println("Test Case 4 - Invalid Request:");
        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody());
    }
}