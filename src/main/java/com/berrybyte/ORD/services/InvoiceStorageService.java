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

public class InvoiceStorageService {

    private static final Duration PRESIGNED_URL_DURATION = Duration.ofDays(7);

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

    private void uploadObject(StorageConfig config, PutObjectRequest putObjectRequest, Path pdfPath) {
        try (S3Client s3Client = buildS3Client(config)) {
            s3Client.putObject(putObjectRequest, RequestBody.fromFile(pdfPath));
        }
    }

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

    private String buildObjectKey(InvoiceDetails invoiceDetails) {
        return "invoices/order-%d/invoice-%d-order-%d.pdf".formatted(
                invoiceDetails.getOrderId(),
                invoiceDetails.getInvoiceId(),
                invoiceDetails.getOrderId()
        );
    }

    private Map<String, String> buildMetadata(InvoiceDetails invoiceDetails) {
        Map<String, String> metadata = new LinkedHashMap<>();
        metadata.put("invoice-id", String.valueOf(invoiceDetails.getInvoiceId()));
        metadata.put("order-id", String.valueOf(invoiceDetails.getOrderId()));
        metadata.put("merchant-id", String.valueOf(invoiceDetails.getMerchantId()));
        metadata.put("merchant-account", blankSafe(invoiceDetails.getIposAccountNumber()));
        return metadata;
    }

    private String blankSafe(String value) {
        return value == null || value.isBlank() ? "n-a" : value;
    }

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

    private record StorageConfig(
            URI endpointUri,
            Region region,
            String bucketName,
            StaticCredentialsProvider credentialsProvider,
            boolean pathStyleAccessEnabled
    ) {
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

        private StorageConfig withEndpoint(URI newEndpointUri) {
            return new StorageConfig(newEndpointUri, region, bucketName, credentialsProvider, pathStyleAccessEnabled);
        }

        private StorageConfig withPathStyle(boolean newPathStyleAccessEnabled) {
            return new StorageConfig(endpointUri, region, bucketName, credentialsProvider, newPathStyleAccessEnabled);
        }

        private static void addCandidate(List<StorageConfig> candidates, Set<String> seen, StorageConfig config) {
            String fingerprint = config.endpointUri + "|" + config.pathStyleAccessEnabled;
            if (seen.add(fingerprint)) {
                candidates.add(config);
            }
        }

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
