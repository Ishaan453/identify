package com.bitespeed.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.bitespeed.model.IdentifyRequest;
import com.bitespeed.model.IdentifyResponse;
import com.bitespeed.service.IdentifyService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class IdentityHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final IdentifyService identifyService = new IdentifyService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        System.out.println(input);
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        response.setHeaders(headers);

        try {
            // Parse the incoming request
            IdentifyRequest request = objectMapper.readValue(input.getBody(), IdentifyRequest.class);
            String email = request.getEmail();
            String phoneNumber = request.getPhoneNumber();

            // Validate input: at least one of email or phoneNumber must be present
            if ((email == null || email.isEmpty()) && (phoneNumber == null || phoneNumber.isEmpty())) {
                response.setStatusCode(400);
                response.setBody("{\"error\":\"At least one of email or phoneNumber must be provided\"}");
                return response;
            }

            // Call the service to process the request
            IdentifyResponse identifyResponse = identifyService.identify(email, phoneNumber);

            // Return the response
            response.setStatusCode(200);
            response.setBody(objectMapper.writeValueAsString(identifyResponse));
        } catch (Exception e) {
            context.getLogger().log("Error: " + e.getMessage());
            response.setStatusCode(500);
            response.setBody("{\"error\":\"Internal Server Error\"}");
        }

        return response;
    }
}