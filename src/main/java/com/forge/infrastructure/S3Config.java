package com.forge.infrastructure;

import java.net.URI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/**
 * Configures the S3 client and presigner beans for interacting with AWS S3.
 */
@Configuration
public class S3Config {
    private final S3Properties s3Properties;

    public S3Config(S3Properties s3Properties) {
        this.s3Properties = s3Properties;
    }

    /**
     * Wraps a fixed set of AWS credentials in a StaticCredentialsProvider
     * (ideal for local emulators) for use with the S3 client and presigner.
     * @return a StaticCredentialsProvider containing the configured access and secret keys
     */
    private StaticCredentialsProvider credentialsProvider() {
        return StaticCredentialsProvider.create(
            AwsBasicCredentials.create(this.s3Properties.accessKey(), this.s3Properties.secretKey())
        );
    }

    /**
     * Configures the S3 client to use path-style access so the bucket name
     * is not prepended as a subdomain, which is necessary for LocalStack.
     * @return the S3Configuration with path-style access enabled
     */
    private S3Configuration pathStyle() {
        return S3Configuration.builder().pathStyleAccessEnabled(true).build();
    }

    /**
     * Creates and configures an S3Client bean for interacting with AWS S3 in backend operations.
     * @return an S3Client configured with the internal endpoint, region, credentials, and path-style access
     */
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
            .endpointOverride(URI.create(this.s3Properties.internalEndpoint()))
            .region(Region.of(this.s3Properties.region()))
            .credentialsProvider(credentialsProvider())
            .serviceConfiguration(pathStyle())
            .build();
    }

    /**
     * Creates and configures an S3Presigner bean for generating pre-signed URLs for S3 objects on the client side.
     * @return an S3Presigner configured with the external endpoint, region, credentials, and path-style access
     */
    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
            .endpointOverride(URI.create(this.s3Properties.externalEndpoint()))
            .region(Region.of(this.s3Properties.region()))
            .credentialsProvider(credentialsProvider())
            .serviceConfiguration(pathStyle())
            .build();
    }
}
