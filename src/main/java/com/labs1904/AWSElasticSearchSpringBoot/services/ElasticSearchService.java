package com.labs1904.AWSElasticSearchSpringBoot.services;

import com.amazonaws.*;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.http.ExecutionContext;
import com.amazonaws.http.HttpMethodName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.labs1904.AWSElasticSearchSpringBoot.config.ConfigurationInfo;
import com.labs1904.AWSElasticSearchSpringBoot.handlers.AwsResponse;
import com.labs1904.AWSElasticSearchSpringBoot.handlers.ElasticSearchClientHandler;
import com.labs1904.AWSElasticSearchSpringBoot.models.Movie;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named
public class ElasticSearchService {

    private static final AWSCredentials AWS_CREDENTIALS = new DefaultAWSCredentialsProviderChain().getCredentials();

    @Inject
    private ConfigurationInfo configurationInfo;

    private void signRequest(Request request) {
        final String region = configurationInfo.getRegion();
        final String serviceName = configurationInfo.getServiceName();

        final AWS4Signer aws4Signer = new AWS4Signer();
        aws4Signer.setRegionName(region);
        aws4Signer.setServiceName(serviceName);
        aws4Signer.sign(request, AWS_CREDENTIALS);
    }

    public Request generateSignedRequest(final String url,
                                         final String json,
                                         final Map<String, List<String>> parameters,
                                         final HttpMethodName httpMethodName) {

        final String endpoint = configurationInfo.getEndpoint() + "/" + url;
        final Map<String, String> headers = new HashMap<>();
        headers.put("content-type", "application/json");

        final Request request = new DefaultRequest(configurationInfo.getServiceName());
        request.setHeaders(headers);

        // JSON is used for Creating and Updating objects in ElasticSearch
        if (json != null) {
            request.setContent(new ByteArrayInputStream(json.getBytes()));
        }
        // Parameters are used for queries
        if (parameters != null) {
            request.setParameters(parameters);
        }
        request.setEndpoint(URI.create(endpoint));
        request.setHttpMethod(httpMethodName);

        signRequest(request);

        return request;
    }

    private AwsResponse executeRequest(Request request) {
        try {
            final ClientConfiguration configuration = new ClientConfiguration();
            final ExecutionContext context = new ExecutionContext(true);
            final ElasticSearchClientHandler client = new ElasticSearchClientHandler(configuration);

            return client.execute(context, request);
        } catch (Exception e) {
            System.out.println("Error executing ElasticSearch Request." + e);
        }
        return null;
    }

    private AwsResponse createDocument(final String index, final String type, final String json, final String id) {
        final String url = index + "/" + type + "/" + id;
        final Request request = generateSignedRequest(url, json, null, HttpMethodName.PUT);

        return executeRequest(request);
    }

    public String createNewMovie(Movie movie) throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final String json = objectMapper.writeValueAsString(movie);
        if (json != null) {
            AwsResponse response = createDocument("movies", "movie", json, Long.toString(movie.getId()));
            if (response != null && response.getHttpResponse().getStatusCode() >= 200
                    && response.getHttpResponse().getStatusCode() < 300) {
                System.out.println("Successfully created new movie with ID: " + movie.getId() + " and title: " + movie.getTitle());
                return movie.getTitle();
            }
        }

        return null;
    }

}
