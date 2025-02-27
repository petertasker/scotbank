package uk.co.asepstrath.bank.services.data;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;

public class UnirestWrapper {
    public HttpResponse<String> get(String url) {
        return Unirest.get(url).asString();
    }
}
