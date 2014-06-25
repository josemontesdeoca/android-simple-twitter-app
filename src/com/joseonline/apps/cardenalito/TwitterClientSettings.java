package com.joseonline.apps.cardenalito;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

public class TwitterClientSettings {
    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
    public static final String REST_URL = "https://api.twitter.com/1.1";
    public static final String REST_CONSUMER_KEY = "SOME_KEY";
    public static final String REST_CONSUMER_SECRET = "SOME_SECRET";
    public static final String REST_CALLBACK_URL = "oauth://cardenalito";
}
