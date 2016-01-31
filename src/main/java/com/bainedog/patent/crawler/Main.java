package com.bainedog.patent.crawler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    // https://data.uspto.gov/uspto.html
    // https://bulkdata.uspto.gov/data2/patent/grant/redbook/fulltext/2016/

    static final String url = "https://bulkdata.uspto.gov/data2/patent/grant/redbook/fulltext/2014/ipg140107.zip";
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        CacheRequest request = new CacheRequest();
        request.setUrl(url);
        new Handler().myHandler(request, null);
    }
}
