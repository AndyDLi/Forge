package com.forge.infrastructure;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;

/**
 * Health indicator for checking the availability of the configured S3 bucket exposed at /actuator/health.
 */
@Component
public class S3HealthIndicator implements HealthIndicator {
    private final S3Client s3Client;
    private final S3Properties s3Properties;

    public S3HealthIndicator(S3Client s3Client, S3Properties s3Properties) {
        this.s3Client = s3Client;
        this.s3Properties = s3Properties;
    }

    /**
     * Executes a lightweight headBucket request to the configured target S3 bucket.
     * @return Health.up() with the bucket if successful, or Health.down() with the error and bucket otherwise
     */
    @Override
    public Health health() {
        try {
            this.s3Client.headBucket(HeadBucketRequest.builder().bucket(this.s3Properties.bucket()).build());
            return Health.up().withDetail("bucket", this.s3Properties.bucket()).build();
        } catch (Exception ex) {
            return Health.down(ex).withDetail("bucket", this.s3Properties.bucket()).build();
        }
    }
}
