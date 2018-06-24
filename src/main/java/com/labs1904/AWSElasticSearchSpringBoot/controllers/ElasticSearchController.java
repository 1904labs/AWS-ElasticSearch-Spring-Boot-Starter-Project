package com.labs1904.AWSElasticSearchSpringBoot.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.labs1904.AWSElasticSearchSpringBoot.models.Movie;
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
     * @param query The query
     * @return Set of Movies
     */
    @GetMapping(value = "/get", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Set<Movie> getFromElasticSearch(@RequestParam("query") final String query) {
        return new HashSet<Movie>();
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
     * @param id The Movie ID
     * @return Response Entity
     */
    @DeleteMapping(value = "/delete", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity<String> deleteFromElasticSearch(@RequestParam("query") final String id) {
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }
}
