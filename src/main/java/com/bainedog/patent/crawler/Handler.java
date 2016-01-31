package com.bainedog.patent.crawler;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class Handler {

    private static String n(String header) {
        return header.replaceAll("-", "").toLowerCase();
    }

    private static final Logger logger = LoggerFactory.getLogger(Handler.class);

    public String myHandler(CacheRequest request, Context context)
            throws IOException {
        logger.info("hello");

        String uri = request.url;
        HeadMetadata hm = new HeadMetadata();
        AmazonS3 s3 = new AmazonS3Client();

        try (CloseableHttpClient http = HttpClients.createDefault()) {
            logger.info("begin HEAD of {}", uri);
            HttpUriRequest head = new HttpHead(uri);
            CloseableHttpResponse response = http.execute(head);
            for (Header h : response.getAllHeaders()) {
                logger.info("{} -> {}", h.getName(), h.getValue());
                String n = n(h.getName());
                if ("etag".equals(n)) {
                    hm.setEtag(h.getValue());
                } else if ("contentlength".equals(n)) {
                    hm.setContentLength(Long.parseLong(h.getValue()));
                } else if ("contentmd5".equals(n)) {
                    hm.setMd5(h.getValue());
                }
            }
            EntityUtils.consume(response.getEntity());
            response.close();
            logger.info("finished HEAD of {}", uri);

            String bucketName = "patent-cache-us-east-1-prod";
            String key = uri.replaceAll("http://", "").replaceAll("https://",
                    "");
            GetObjectMetadataRequest r = new GetObjectMetadataRequest(
                    bucketName, key);

            logger.info("begin s3 HEAD of s3://{}/{}", bucketName, key);
            try {
                ObjectMetadata s3meta = s3.getObjectMetadata(r);
                S3Metadata s3Metadata = new S3Metadata();
                s3Metadata.setContentLength(s3meta.getContentLength());
                s3Metadata.setMd5(s3meta.getContentMD5());
                s3Metadata.setEtag(s3meta.getETag());
                s3Metadata.setSourceEtag(s3meta
                        .getUserMetaDataOf("source-etag"));
            } catch (com.amazonaws.services.s3.model.AmazonS3Exception e) {
                logger.error("exception calling s3", e);
            }
            logger.info("finished s3 HEAD of s3://{}/{}", bucketName, key);

            logger.info("begin s3 PUT of {}", uri);
            HttpUriRequest get = new HttpGet(uri);
            CloseableHttpResponse getResponse = http.execute(head);
            InputStream inputStream = getResponse.getEntity().getContent();
            ObjectMetadata om = new ObjectMetadata();
            om.setContentLength(hm.getContentLength());
            PutObjectRequest por = new PutObjectRequest(bucketName, key,
                    inputStream, om);
            s3.putObject(por);
            logger.info("finished s3 PUT of {}", uri);
        }

        return "SUCCESS";
    }
}
