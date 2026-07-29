package dev.abhishek.ecommerce.common.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration for the RustFS (S3 compatible) object store.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "storage")
public class StorageProperties {

    /**
     * Base URL of the RustFS S3 API, e.g. http://localhost:9000
     */
    private String endpoint = "http://localhost:9000";

    /**
     * RustFS ignores the region but the AWS SDK requires one to sign requests.
     */
    private String region = "us-east-1";

    private String accessKey;

    private String secretKey;

    private String bucket = "ecommerce";

    /**
     * Public base URL used to build download links. Defaults to {@link #endpoint} when unset,
     * which is what you want unless RustFS sits behind a reverse proxy or CDN.
     */
    private String publicUrl;

    /**
     * RustFS addresses buckets by path ({@code /bucket/key}), not by virtual host.
     */
    private boolean pathStyleAccess = true;

    /**
     * Create the bucket at startup when it does not exist yet.
     */
    private boolean autoCreateBucket = true;

    /**
     * Reject uploads larger than this many bytes.
     */
    private long maxFileSize = 5L * 1024 * 1024;

    public String resolvedPublicUrl() {
        String base = (publicUrl == null || publicUrl.isBlank()) ? endpoint : publicUrl;
        return base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
    }
}
