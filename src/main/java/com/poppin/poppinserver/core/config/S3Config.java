package com.poppin.poppinserver.core.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final AwsProperties awsProperties;

    @Bean
    public AmazonS3Client amazonS3Client() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(
                awsProperties.getCredentials().getAWS_ACCESS_KEY_ID(),
                awsProperties.getCredentials().getAWS_SECRET_ACCESS_KEY()
        );

        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(awsProperties.getRegion().getAWS_REGION())
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}
