package com.bainedog.patent.crawler;

import com.amazonaws.services.lambda.runtime.Context;

public class Handler {

    public String myHandler(int count, Context context) {
        return "Hello, lambda, you said: " + String.valueOf(count);
    }
}
