package com.labs1904.aws.elasticsearch.springboot.exceptions;

public class IdNotFoundException extends Exception {
    public IdNotFoundException(String message) {
        super(message);
    }
}
