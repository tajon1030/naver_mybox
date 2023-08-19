package com.numble.mybox.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class S3Client {

    private final AmazonS3 amazonS3;

    private final String url;

    private final String bucketName;

    public S3Object get(String key) {
        GetObjectRequest request = new GetObjectRequest(bucketName, key);
        return amazonS3.getObject(request);
    }

    /**
     * 파일 업로드
     *
     * @param in          InputStream
     * @param length      byteSize
     * @param key         저장이름(폴더 path 포함)
     * @param contentType contentType
     * @param metadata    Map.of("oriFileNm","",...)
     * @return
     */
    public String upload(InputStream in, long length, String key,
                         String contentType, Map<String, String> metadata) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(length);
        objectMetadata.setContentType(contentType);
        if (metadata != null && !metadata.isEmpty()) {
            objectMetadata.setUserMetadata(metadata);
        }
        PutObjectRequest request = new PutObjectRequest(bucketName, key, in, objectMetadata);
        amazonS3.putObject(request.withCannedAcl(CannedAccessControlList.PublicRead));
        log.info("Object %s has been created.", metadata.get("oriFileNm"));
        return key;
    }

    /**
     * 파일 삭제
     *
     * @param url 삭제할 파일 이름(폴더 path 포함)
     */
    public void delete(String... url) {
        amazonS3.deleteObjects(new DeleteObjectsRequest(bucketName).withKeys(url));
    }
}
