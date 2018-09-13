package com.labs1904.aws.elasticsearch.springboot.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.labs1904.aws.elasticsearch.springboot.handlers.AwsResponse;
import com.labs1904.aws.elasticsearch.springboot.models.Movie;
import com.labs1904.aws.elasticsearch.springboot.models.MovieQuery;
import com.labs1904.aws.elasticsearch.springboot.services.ElasticSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@RestController
@RequestMapping("/elastic-search")
public class ElasticSearchController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchController.class);

    @Inject
    private ElasticSearchService elasticSearchService;

    /**
     * Get a Set of Movies that match your query criteria
     *
     * @param movieQuery The query
     * @return Set of Movies
     */
    @PostMapping(value = "/search", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity<String> getFromElasticSearch(@RequestBody final MovieQuery movieQuery) {
        return ResponseEntity.status(HttpStatus.OK).body(
                elasticSearchService.getMovies("movies", 0, 100, null, movieQuery));
    }

    /**
     * Fuzzy search the Movies index with a partial word, or one word in a sentence.
     *
     * @param movieQuery The query
     * @return Set of Movies
     */
    @PostMapping(value = "/fuzzySearch", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity<String> getFromElasticSearchFuzzySearch(@RequestBody final MovieQuery movieQuery) {
        return ResponseEntity.status(HttpStatus.OK).body(
                elasticSearchService.getMoviesFuzzySearch("movies", 0, 100, null, movieQuery));
    }

    /**
     * Create a new Movie in ElasticSearch
     *
     * @param movie The Movie object
     * @return Response Entity
     */
    @PostMapping(value = "/create", produces = {MediaType.TEXT_PLAIN_VALUE})
    @ResponseBody
    public ResponseEntity<String> createElasticSearchObject(@RequestBody final Movie movie) {
        String title = null;
        try {
            title = elasticSearchService.createNewMovie(movie);
            if (title != null) {
                return ResponseEntity.status(HttpStatus.OK).body("Successfully created " + title);
            }
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to create Movie.", e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create  " + title);
    }

    /**
     * Update a Movie object in ElasticSearch
     *
     * @param movie The Movie object
     * @return Response Entity
     */
    @PutMapping(value = "/update", produces = {MediaType.TEXT_PLAIN_VALUE})
    @ResponseBody
    public ResponseEntity<String> updateElasticSearchObject(@RequestBody final Movie movie) {
        String title = null;
        try {
            // For simplicity, since ElasticSearch fully overwrites the document, we will create a new document from the request
            // and push to ElasticSearch which will overwrite the existing document
            title = elasticSearchService.createNewMovie(movie);
            if (title != null) {
                return ResponseEntity.status(HttpStatus.OK).body("Successfully updated " + title);
            }
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to update Movie.", e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update  " + title);
    }

    /**
     * Delete a Movie object in ElasticSearch
     *
     * @param index The targeted index
     * @param type  The document type
     * @param id    The document ID
     * @return Response Entity
     */
    @DeleteMapping(value = "/delete", produces = {MediaType.TEXT_PLAIN_VALUE})
    @ResponseBody
    public ResponseEntity<String> deleteFromElasticSearch(@RequestParam("index") final String index,
                                                          @RequestParam("type") final String type,
                                                          @RequestParam("id") final String id) {
        AwsResponse response = elasticSearchService.deleteDocument(index, type, id);
        if (response != null && response.getHttpResponse().getStatusCode() == 200) {
            return ResponseEntity.status(HttpStatus.OK).body(String.valueOf(response.getHttpResponse().getStatusCode()));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting ElasticSearch document");
        }
    }

    /**
     * Get statistics about an ElasticSearch Index
     *
     * @param index The targeted index
     * @return Response Entity
     */
    @GetMapping(value = "/statistics", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity<String> indexStatistics(@RequestParam("index") final String index) {
        String response = elasticSearchService.getIndexStatistics(index);
        if (response != null) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching statistics for index");
        }
    }
}
