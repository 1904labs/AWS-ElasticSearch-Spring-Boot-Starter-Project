package com.labs1904.AWSElasticSearchSpringBoot.handlers;

import com.amazonaws.http.HttpResponse;
import com.amazonaws.util.IOUtils;

import java.io.IOException;

public class AwsResponse {
    private final HttpResponse httpResponse;
    private final String body;

    public AwsResponse(HttpResponse httpResponse) throws IOException {
        this.httpResponse = httpResponse;
        this.body = IOUtils.toString(httpResponse.getContent());
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }

    public String getBody() {
        return body;
    }
}
