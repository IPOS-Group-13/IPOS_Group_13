package com.berrybyte.ORD.services;

import com.berrybyte.ORD.helpers.InvoiceDetails;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents invoice storage service.
 */
public class InvoiceStorageService {

    private static final Duration PRESIGNED_URL_DURATION = Duration.ofDays(7);
/**
 * Performs upload invoice and create access url.
 * This method coordinates the main operation for this action.
 *
 * @param invoiceDetails invoice details
 * @param pdfPath pdf path
 * @return result value
 * @throws Exception when the operation fails
 */

    public String uploadInvoiceAndCreateAccessUrl(InvoiceDetails invoiceDetails, Path pdfPath) throws Exception {
        if (invoiceDetails == null) {
            throw new IllegalArgumentException("Invoice details are required.");
        }
        if (pdfPath == null || !Files.exists(pdfPath)) {
            throw new IllegalArgumentException("Invoice PDF file is required.");
        }

        StorageConfig config = StorageConfig.load();
        List<StorageConfig> candidateConfigs = config.expandCandidates();
        String objectKey = buildObjectKey(invoiceDetails);
        String fileName = pdfPath.getFileName().toString();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(config.bucketName())
                .key(objectKey)
                .contentType("application/pdf")
                .metadata(buildMetadata(invoiceDetails))
                .build();
        Exception lastFailure = null;

        for (StorageConfig candidateConfig : candidateConfigs) {
            try {
                uploadObject(candidateConfig, putObjectRequest, pdfPath);
                return createPresignedUrl(candidateConfig, fileName, objectKey);
            } catch (Exception candidateFailure) {
                lastFailure = candidateFailure;
                if (!isSignatureMismatch(candidateFailure)) {
                    throw candidateFailure;
                }
            }
        }

        throw lastFailure == null
                ? new IllegalStateException("Invoice upload failed before a presigned URL could be created.")
                : lastFailure;
    }
/**
 * Performs upload object.
 * This method coordinates the main operation for this action.
 *
 * @param config config
 * @param putObjectRequest put object request
 * @param pdfPath pdf path
 */

    private void uploadObject(StorageConfig config, PutObjectRequest putObjectRequest, Path pdfPath) {
        try (S3Client s3Client = buildS3Client(config)) {
            s3Client.putObject(putObjectRequest, RequestBody.fromFile(pdfPath));
        }
    }
/**
 * Executes the create presigned url workflow.
 * This method coordinates the main operation for this action.
 *
 * @param config config
 * @param fileName file name
 * @param objectKey object key
 * @return result value
 */

    private String createPresignedUrl(StorageConfig config, String fileName, String objectKey) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(config.bucketName())
                .key(objectKey)
                .responseContentType("application/pdf")
                .responseContentDisposition("inline; filename=\"" + fileName + "\"")
                .build();

        try (S3Presigner presigner = buildPresigner(config)) {
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(PRESIGNED_URL_DURATION)
                    .getObjectRequest(getObjectRequest)
                    .build();

            return presigner.presignGetObject(presignRequest).url().toExternalForm();
        }
    }
/**
 * Performs build s3 client.
 *
 * @param config config
 * @return result value
 */

    private S3Client buildS3Client(StorageConfig config) {
        return S3Client.builder()
                .endpointOverride(config.endpointUri())
                .region(config.region())
                .credentialsProvider(config.credentialsProvider())
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(config.pathStyleAccessEnabled())
                        .build())
                .build();
    }
/**
 * Performs build presigner.
 *
 * @param config config
 * @return result value
 */

    private S3Presigner buildPresigner(StorageConfig config) {
        return S3Presigner.builder()
                .endpointOverride(config.endpointUri())
                .region(config.region())
                .credentialsProvider(config.credentialsProvider())
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(config.pathStyleAccessEnabled())
                        .build())
                .build();
    }
/**
 * Performs build object key.
 *
 * @param invoiceDetails invoice details
 * @return result value
 */

    private String buildObjectKey(InvoiceDetails invoiceDetails) {
        return "invoices/order-%d/invoice-%d-order-%d.pdf".formatted(
                invoiceDetails.getOrderId(),
                invoiceDetails.getInvoiceId(),
                invoiceDetails.getOrderId()
        );
    }
/**
 * Performs build metadata.
 *
 * @param invoiceDetails invoice details
 * @return result value
 */

    private Map<String, String> buildMetadata(InvoiceDetails invoiceDetails) {
        Map<String, String> metadata = new LinkedHashMap<>();
        metadata.put("invoice-id", String.valueOf(invoiceDetails.getInvoiceId()));
        metadata.put("order-id", String.valueOf(invoiceDetails.getOrderId()));
        metadata.put("merchant-id", String.valueOf(invoiceDetails.getMerchantId()));
        metadata.put("merchant-account", blankSafe(invoiceDetails.getIposAccountNumber()));
        return metadata;
    }
/**
 * Performs blank safe.
 *
 * @param value value
 * @return result value
 */

    private String blankSafe(String value) {
        return value == null || value.isBlank() ? "n-a" : value;
    }
/**
 * Performs is signature mismatch.
 *
 * @param exception exception
 * @return result value
 */

    private boolean isSignatureMismatch(Exception exception) {
        Throwable current = exception;
        while (current != null) {
            if (current instanceof S3Exception s3Exception && s3Exception.statusCode() == 403) {
                String message = s3Exception.getMessage();
                if (message != null && message.toLowerCase().contains("signature")) {
                    return true;
                }
            }
            String message = current.getMessage();
            if (message != null && message.toLowerCase().contains("signature")) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

/**
 * Represents immutable data for storage config.
 */
    private record StorageConfig(
            URI endpointUri,
            Region region,
            String bucketName,
            StaticCredentialsProvider credentialsProvider,
            boolean pathStyleAccessEnabled
    ) {
/**
 * Performs load.
 *
 * @return result value
 */
        private static StorageConfig load() {
            String endpoint = requireValue("railway.bucket.endpoint",
                    "Endpoint URL",
                    "RAILWAY_BUCKET_ENDPOINT", "ENDPOINT");
            String region = requireValue("railway.bucket.region",
                    "Region",
                    "RAILWAY_BUCKET_REGION", "REGION");
            String bucketName = requireValue("railway.bucket.name",
                    "Bucket Name",
                    "BUCKET", "RAILWAY_BUCKET_NAME");
            String accessKeyId = requireValue("railway.access.key.id",
                    "Access Key ID",
                    "RAILWAY_ACCESS_KEY_ID", "ACCESS_KEY_ID", "AWS_ACCESS_KEY_ID");
            String secretAccessKey = requireValue("railway.secret.access.key",
                    "Secret Access Key",
                    "RAILWAY_SECRET_ACCESS_KEY", "SECRET_ACCESS_KEY", "AWS_SECRET_ACCESS_KEY");
            boolean pathStyleAccessEnabled = resolveBoolean(
                    "railway.bucket.path.style",
                    false,
                    "RAILWAY_BUCKET_PATH_STYLE");

            AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);

            return new StorageConfig(
                    URI.create(endpoint),
                    Region.of(region),
                    bucketName,
                    StaticCredentialsProvider.create(credentials),
                    pathStyleAccessEnabled
            );
        }
/**
 * Performs expand candidates.
 *
 * @return result value
 */

        private List<StorageConfig> expandCandidates() {
            List<StorageConfig> candidates = new ArrayList<>();
            Set<String> seen = new LinkedHashSet<>();

            addCandidate(candidates, seen, this);

            URI railwayDefaultEndpoint = URI.create("https://storage.railway.app");
            if (!endpointUri.equals(railwayDefaultEndpoint)) {
                addCandidate(candidates, seen, withEndpoint(railwayDefaultEndpoint));
            }
            addCandidate(candidates, seen, withPathStyle(!pathStyleAccessEnabled));

            if (!endpointUri.equals(railwayDefaultEndpoint)) {
                addCandidate(candidates, seen, withEndpoint(railwayDefaultEndpoint).withPathStyle(!pathStyleAccessEnabled));
            }
            return candidates;
        }
/**
 * Performs with endpoint.
 *
 * @param newEndpointUri new endpoint uri
 * @return result value
 */

        private StorageConfig withEndpoint(URI newEndpointUri) {
            return new StorageConfig(newEndpointUri, region, bucketName, credentialsProvider, pathStyleAccessEnabled);
        }
/**
 * Performs with path style.
 *
 * @param newPathStyleAccessEnabled new path style access enabled
 * @return result value
 */

        private StorageConfig withPathStyle(boolean newPathStyleAccessEnabled) {
            return new StorageConfig(endpointUri, region, bucketName, credentialsProvider, newPathStyleAccessEnabled);
        }
/**
 * Performs add candidate.
 * This method coordinates the main operation for this action.
 *
 * @param candidates candidates
 * @param seen seen
 * @param config config
 */

        private static void addCandidate(List<StorageConfig> candidates, Set<String> seen, StorageConfig config) {
            String fingerprint = config.endpointUri + "|" + config.pathStyleAccessEnabled;
            if (seen.add(fingerprint)) {
                candidates.add(config);
            }
        }
/**
 * Performs require value.
 * This method coordinates the main operation for this action.
 *
 * @param systemPropertyName system property name
 * @param displayName display name
 * @param environmentNames environment names
 * @return result value
 */

        private static String requireValue(String systemPropertyName, String displayName, String... environmentNames) {
            String systemPropertyValue = System.getProperty(systemPropertyName);
            if (systemPropertyValue != null && !systemPropertyValue.isBlank()) {
                return systemPropertyValue.trim();
            }

            for (String environmentName : environmentNames) {
                String environmentValue = System.getenv(environmentName);
                if (environmentValue != null && !environmentValue.isBlank()) {
                    return environmentValue.trim();
                }
            }
            throw new IllegalStateException(
                    "Missing bucket configuration for %s. Set one of: %s"
                            .formatted(displayName, String.join(", ", environmentNames)));
        }
/**
 * Performs resolve boolean.
 * This method coordinates the main operation for this action.
 *
 * @param systemPropertyName system property name
 * @param defaultValue default value
 * @param environmentNames environment names
 * @return result value
 */

        private static boolean resolveBoolean(String systemPropertyName, boolean defaultValue, String... environmentNames) {
            String systemPropertyValue = System.getProperty(systemPropertyName);
            if (systemPropertyValue != null && !systemPropertyValue.isBlank()) {
                return Boolean.parseBoolean(systemPropertyValue.trim());
            }

            for (String environmentName : environmentNames) {
                String environmentValue = System.getenv(environmentName);
                if (environmentValue != null && !environmentValue.isBlank()) {
                    return Boolean.parseBoolean(environmentValue.trim());
                }
            }
            return defaultValue;
        }
    }
}
