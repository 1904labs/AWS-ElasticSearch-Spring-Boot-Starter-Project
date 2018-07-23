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
import com.labs1904.AWSElasticSearchSpringBoot.constants.ElasticSearchConstants;
import com.labs1904.AWSElasticSearchSpringBoot.handlers.AwsResponse;
import com.labs1904.AWSElasticSearchSpringBoot.handlers.ElasticSearchClientHandler;
import com.labs1904.AWSElasticSearchSpringBoot.models.Movie;
import com.labs1904.AWSElasticSearchSpringBoot.models.MovieQuery;
import com.labs1904.AWSElasticSearchSpringBoot.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.*;

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
            AwsResponse response = createDocument(ElasticSearchConstants.MOVIES_INDEX,
                    ElasticSearchConstants.MOVIES_DOCUMENT_TYPE,
                    json,
                    Long.toString(movie.getId()));
            if (response != null && response.getHttpResponse().getStatusCode() >= 200
                    && response.getHttpResponse().getStatusCode() < 300) {
                System.out.println("Successfully created new movie with ID: " + movie.getId() + " and title: " + movie.getTitle());
                return movie.getTitle();
            }
        }

        return null;
    }

    /**
     *
     * @param from Beginning point of the query
     * @param size Number of objects to return in the query
     * @param filterValues Optional list of values to filter the response by
     * @return Response
     */
    public String getMovies(final String index, final int from, final int size, Set<String> filterValues, final MovieQuery movieQuery) {
        JSONObject query = new JSONObject();
        JSONObject bool = new JSONObject();
        JSONObject must = new JSONObject();
        JSONArray array = new JSONArray();

        createMovieQuery(movieQuery, array);

        query.put("from", from);
        query.put("size", size);
        must.put("must", array);
        bool.put("bool", must);
        query.put("query", bool);
        if (filterValues != null) {
            query.put("_source", filterValues);
        }

        final Map<String, List<String>> parameters = new HashMap<>();
        parameters.put(ElasticSearchConstants.FILTER_PATH, Collections.singletonList(ElasticSearchConstants.FILTER));

        final String url = index + ElasticSearchConstants.SEARCH_API;
        System.out.println("ES Query Body: " + query.toString());
        final Request request = generateSignedRequest(url, query.toString(), parameters, HttpMethodName.GET);

        final AwsResponse response = executeRequest(request);

        return response != null ? response.getBody() : "";
    }

    /**
     * Build a fuzzy search ElasticSearch query
     *
     * @param from Beginning point of the query
     * @param size Number of objects to return in the query
     * @param filterValues Optional list of values to filter the response by
     * @return Response
     */
    public String getMoviesFuzzySearch(final String index, final int from, final int size, Set<String> filterValues, final MovieQuery movieQuery) {
        JSONObject query = new JSONObject();
        JSONObject fuzzy = new JSONObject();
        JSONObject searchTerm = new JSONObject();

        createMovieQueryFuzzySearch(movieQuery, searchTerm);

        query.put("from", from);
        query.put("size", size);
        fuzzy.put("fuzzy", searchTerm);
        query.put("query", fuzzy);
        if (filterValues != null) {
            query.put("_source", filterValues);
        }

        final Map<String, List<String>> parameters = new HashMap<>();
        parameters.put(ElasticSearchConstants.FILTER_PATH, Collections.singletonList(ElasticSearchConstants.FILTER));

        final String url = index + ElasticSearchConstants.SEARCH_API;
        System.out.println("ES Query Body: " + query.toString());
        final Request request = generateSignedRequest(url, query.toString(), parameters, HttpMethodName.GET);

        final AwsResponse response = executeRequest(request);

        return response != null ? response.getBody() : "";
    }

    private void createMovieQuery(final MovieQuery movieQuery, JSONArray array) {
        if(movieQuery.getId() > 0){
            buildElasticSearchMatchStatement("id", movieQuery.getId(), array);
        }
        if(StringUtils.checkNullOrEmpty(movieQuery.getTitle())){
            buildElasticSearchMatchStatement("title", movieQuery.getTitle(), array);
        }
        if(movieQuery.getYear() > 0){
            buildElasticSearchMatchStatement("year", movieQuery.getYear(), array);
        }
        if(movieQuery.getGenre() != null){
            buildElasticSearchShouldStatement("genre", movieQuery.getGenre(), array);
        }
        if(StringUtils.checkNullOrEmpty(movieQuery.getMpaaRating())){
            buildElasticSearchMatchStatement("mpaaRating", movieQuery.getMpaaRating(), array);
        }
        if(StringUtils.checkNullOrEmpty(movieQuery.getImdbUrl())){
            buildElasticSearchMatchStatement("imdbUrl", movieQuery.getImdbUrl(), array);
        }
        if(StringUtils.checkNullOrEmpty(movieQuery.getLanguage())){
            buildElasticSearchMatchStatement("language", movieQuery.getLanguage(), array);
        }
        if(StringUtils.checkNullOrEmpty(movieQuery.getCountry())){
            buildElasticSearchMatchStatement("country", movieQuery.getCountry(), array);
        }
        if(StringUtils.checkNullOrEmpty(movieQuery.getStoryline())){
            buildElasticSearchMatchStatement("storyline", movieQuery.getStoryline(), array);
        }
        if(StringUtils.checkNullOrEmpty(movieQuery.getSynopsis())){
            buildElasticSearchMatchStatement("synopsis", movieQuery.getSynopsis(), array);
        }
    }

    private void createMovieQueryFuzzySearch(final MovieQuery movieQuery, JSONObject searchTerm) {
        if(StringUtils.checkNullOrEmpty(movieQuery.getStoryline())){
            buildElasticSearchFuzzyStatement("storyline", movieQuery.getStoryline(), searchTerm);
        }
        if(StringUtils.checkNullOrEmpty(movieQuery.getSynopsis())){
            buildElasticSearchFuzzyStatement("synopsis", movieQuery.getSynopsis(), searchTerm);
        }

    }

    private void buildElasticSearchShouldStatement(final String field, final Collection value,
                                                   final JSONArray array) {
        if (value.size() > 1) {
            final JSONObject bool = new JSONObject();
            final JSONObject should = new JSONObject();
            final JSONArray match = new JSONArray();
            for (Object objectValue : value) {
                buildElasticSearchMatchStatement(field, objectValue, match);
            }
            should.put("should", match);
            bool.put("bool", should);
            array.put(bool);
        } else {
            buildElasticSearchMatchStatement(field, value.iterator().next(), array);
        }
    }

    private void buildElasticSearchMatchStatement(final String field, final Object value, final JSONArray array) {
        final JSONObject matchItem = new JSONObject();
        final JSONObject matchTerms = new JSONObject();
        matchTerms.put(field, value);
        matchItem.put("match", matchTerms);
        array.put(matchItem);
    }

    private void buildElasticSearchFuzzyStatement(final String field, final Object value, final JSONObject searchTerm) {
        final JSONObject fuzzyBlock = new JSONObject();
        fuzzyBlock.put("value", value);
        fuzzyBlock.put("boost", 1.0);
        fuzzyBlock.put("fuzziness", 50);
        fuzzyBlock.put("prefix_length", 0);
        fuzzyBlock.put("max_expansions", 100);
        searchTerm.put(field, fuzzyBlock);
    }

}
