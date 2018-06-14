package com.labs1904.AWSElasticSearchSpringBoot.controllers;

import com.labs1904.AWSElasticSearchSpringBoot.models.Movie;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/elastic-search")
public class ElasticSearchController {

    /**
     * Get a Set of Movies that match your query criteria
     *
     * @param query The query
     * @return Set of Movies
     */
    @GetMapping(value = "/get", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Set<Movie> getFromElasticSearch(@RequestParam("query") final String query){
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
    public ResponseEntity<String> createElasticSearchObject(@RequestBody final Movie movie){
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }

    /**
     * Update a Movie object in ElasticSearch
     *
     * @param movie The Movie object
     * @return Response Entity
     */
    @PutMapping(value = "/update", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity<String> updateElasticSearchObject(@RequestBody final Movie movie){
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
    public ResponseEntity<String> deleteFromElasticSearch(@RequestParam("query") final String id){
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }
}
