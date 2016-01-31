package com.bainedog.patent.crawler;

import lombok.Data;

@Data
public class S3Metadata {

    public long contentLength;
    public String md5;
    public String etag;
    public String sourceEtag;
}
