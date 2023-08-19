package com.numble.mybox.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.numble.mybox.util.S3Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfigure {

    @Bean
    public AmazonS3 amazonS3Client(AwsConfigure awsConfigure) {
        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(awsConfigure.getUrl(), awsConfigure.getRegion()))
                .withCredentials(new AWSStaticCredentialsProvider(
                                new BasicAWSCredentials(
                                        awsConfigure.getAccessKey(),
                                        awsConfigure.getSecretKey())
                        )
                )
                .build();
    }

    @Bean
    public S3Client s3Client(AmazonS3 amazonS3, AwsConfigure awsConfigure) {
        return new S3Client(amazonS3, awsConfigure.getUrl(), awsConfigure.getBucketName());
    }
}
