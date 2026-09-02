package com.forge.infrastructure;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Automatically binds, validates, and stores the S3 endpoint configuration in an object.
 * @param internalEndpoint the internal endpoint for S3 access in the backend network
 * @param externalEndpoint the external endpoint for S3 access from the client side
 * @param region the AWS region where the S3 bucket is located
 * @param accessKey the access key for S3 authentication
 * @param secretKey the secret key for S3 authentication
 * @param bucket the name of the S3 bucket to be used
 */
@Validated
@ConfigurationProperties(prefix = "forge.s3")
public record S3Properties(
    @NotBlank String internalEndpoint,
    @NotBlank String externalEndpoint,
    @NotBlank String region,
    @NotBlank String accessKey,
    @NotBlank String secretKey,
    @NotBlank String bucket
) {

}
