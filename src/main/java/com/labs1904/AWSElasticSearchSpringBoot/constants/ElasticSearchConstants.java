package com.labs1904.AWSElasticSearchSpringBoot.constants;

public class ElasticSearchConstants {
    private ElasticSearchConstants() throws IllegalAccessException {
        throw new IllegalAccessException("ElasticSearchConstants is a utility class");
    }

    public static final String MOVIES_INDEX = "movies";
    public static final String MOVIES_DOCUMENT_TYPE = "movie";

    public static final String FILTER_PATH = "filter_path";
    public static final String FILTER = "hits.hits._source";
    public static final String SEARCH_API = "/_search";

}
