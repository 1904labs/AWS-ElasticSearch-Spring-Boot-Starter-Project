package com.labs1904.AWSElasticSearchSpringBoot.handlers;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.AmazonWebServiceClient;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Request;
import com.amazonaws.http.AmazonHttpClient;
import com.amazonaws.http.ExecutionContext;
import com.amazonaws.http.HttpResponseHandler;
import com.amazonaws.http.JsonResponseHandler;
import com.amazonaws.protocol.json.JsonOperationMetadata;
import com.amazonaws.protocol.json.SdkStructuredPlainJsonFactory;
import com.amazonaws.transform.JsonErrorUnmarshaller;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Collections;

public class ElasticSearchClientHandler extends AmazonWebServiceClient {
    private final HttpResponseHandler<AmazonServiceException> httpResponseHandler;
    private final JsonResponseHandler<AwsResponse> responseHandler;

    public ElasticSearchClientHandler(ClientConfiguration clientConfiguration) {
        super(clientConfiguration);
        AmazonHttpClient client = new AmazonHttpClient(clientConfiguration);
        final JsonOperationMetadata metadata = new JsonOperationMetadata().withHasStreamingSuccessResponse(false).withPayloadJson(false);
        final Unmarshaller<AwsResponse, JsonUnmarshallerContext> responseUnmarshaller = in -> new AwsResponse(in.getHttpResponse());
        this.responseHandler = SdkStructuredPlainJsonFactory.SDK_JSON_FACTORY.createResponseHandler(metadata, responseUnmarshaller);
        JsonErrorUnmarshaller defaultErrorUnmarshaller = new JsonErrorUnmarshaller(AmazonServiceException.class, null) {
            @Override
            public AmazonServiceException unmarshall(JsonNode json) throws Exception {
                return new AmazonServiceException(json.asText());
            }
        };

        this.httpResponseHandler = SdkStructuredPlainJsonFactory.SDK_JSON_FACTORY.createErrorResponseHandler(Collections.singletonList(defaultErrorUnmarshaller), null);
        super.client = client;
    }

    public AwsResponse execute(ExecutionContext context, Request request){
        return this.client.execute(request, responseHandler, httpResponseHandler, context).getAwsResponse();
    }
}
