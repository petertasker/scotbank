package uk.co.asepstrath.bank.services.data;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;

public class UnirestWrapper {
    /**
     * Performs a GET request to the specified URL
     *
     * @param url The base URL to send the request to
     * @return HttpResponse containing the response as a String
     */
    public HttpResponse<String> get(String url) {
        return Unirest.get(url).asString();
    }

    /**
     * Performs a GET request with a single query parameter
     *
     * @param url        The base URL to send the request to
     * @param paramName  The name of the query parameter
     * @param paramValue The value of the query parameter
     * @return HttpResponse containing the response as a String
     */
    public HttpResponse<String> get(String url, String paramName, Object paramValue) {
        return Unirest.get(url).queryString(paramName, paramValue).asString();
    }
}