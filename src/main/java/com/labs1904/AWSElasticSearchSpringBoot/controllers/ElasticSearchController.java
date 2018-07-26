package com.labs1904.AWSElasticSearchSpringBoot.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.labs1904.AWSElasticSearchSpringBoot.handlers.AwsResponse;
import com.labs1904.AWSElasticSearchSpringBoot.models.Movie;
import com.labs1904.AWSElasticSearchSpringBoot.models.MovieQuery;
import com.labs1904.AWSElasticSearchSpringBoot.services.ElasticSearchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/elastic-search")
public class ElasticSearchController {
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
    @PostMapping(value = "/create", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity<String> createElasticSearchObject(@RequestBody final Movie movie) {
        String title = null;
        try {
            title = elasticSearchService.createNewMovie(movie);
            if (title != null) {
                return ResponseEntity.status(HttpStatus.OK).body("Successfully created " + title);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create  " + title);
    }

    /**
     * Update a Movie object in ElasticSearch
     *
     * @param movie The Movie object
     * @return Response Entity
     */
    @PutMapping(value = "/update", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity<String> updateElasticSearchObject(@RequestBody final Movie movie) {
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }

    /**
     * Delete a Movie object in ElasticSearch
     *
     * @param index The targeted index
     * @param type  The document type
     * @param id    The document ID
     * @return Response Entity
     */
    @DeleteMapping(value = "/delete", produces = {MediaType.APPLICATION_JSON_VALUE})
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
}
