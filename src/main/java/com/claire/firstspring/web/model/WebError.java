package com.claire.firstspring.web.model;

public class WebError {

    public String errorMessage;

    public static WebError of(String errorMessage) {
        WebError webError = new WebError();
        webError.errorMessage = errorMessage;
        return webError;
    }
}
