package com.bainedog.patent.crawler;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;

public class Handler {

    private static final Logger logger = LoggerFactory.getLogger(Handler.class);

    // "https://bulkdata.uspto.gov/data2/patent/grant/redbook/fulltext/2014/ipg140107.zip";
    public String myHandler(CacheRequest request, Context context)
            throws IOException {
        logger.info("hello");

        try (CloseableHttpClient http = HttpClients.createDefault()) {
            String uri = request.url;
            HttpUriRequest head = new HttpHead(uri);
            CloseableHttpResponse response = http.execute(head);
            for (Header h : response.getAllHeaders()) {
                System.out.println(h.getName() + " -> " + h.getValue());
            }
            EntityUtils.consume(response.getEntity());
            response.close();
        }
        return "SUCCESS";
    }
}
