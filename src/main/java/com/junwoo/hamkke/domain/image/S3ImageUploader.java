package com.junwoo.hamkke.domain.image;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.junwoo.hamkke.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 *
 * @author junnukim1007gmail.com
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class S3ImageUploader implements ImageUploader{

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public String upload(MultipartFile file, ImageDirectory directory) {
        if (file.isEmpty()) {
            throw new ImageUploaderException(ErrorCode.EMPTY_FILE);
        }

        validateFileSize(file);
        validateImageFile(file);

        String fileName = createFileName(file.getOriginalFilename(), directory);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3Client.putObject(
                    new PutObjectRequest(bucket, fileName, inputStream, metadata)
            );

            String fileUrl = amazonS3Client.getUrl(bucket, fileName).toString();

            log.info("[ImageUploader] 파일 업로드 성공 - fileName: {}, fileUrl: {}", fileName, fileUrl);

            return fileUrl;
        } catch (IOException e) {
            log.error("[ImageUploader] 파일 업로드 실패 - fileName: {}", fileName, e);
            throw new ImageUploaderException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    @Override
    public void delete(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        try {
            String fileName = extractFileNameFromUrl(fileUrl);
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fileName));

            log.info("[ImageUploader] 파일 삭제 성공 - fileName: {}", fileName);
        } catch (Exception e) {
            log.error("[ImageUploader] 파일 삭제 실패 - fileUrl: {}", fileUrl, e);
            throw new ImageUploaderException(ErrorCode.FILE_DELETE_FAILED);
        }

    }

    private String extractFileNameFromUrl(String fileUrl) {
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }

    private String createFileName(String originalFileName, ImageDirectory directory) {
        String extension = extractExtension(originalFileName);
        String uniqueFileName = UUID.randomUUID() + extension;

        return directory + "/" + uniqueFileName;
    }

    private String extractExtension(String originalFileName) {
        if (originalFileName == null || !originalFileName.contains(".")) {
            throw new ImageUploaderException(ErrorCode.INVALID_FILE_EXTENSION);
        }
        return originalFileName.substring(originalFileName.lastIndexOf("."));
    }

    private void validateFileSize(MultipartFile file) {
        long maxSize = 10 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new ImageUploaderException(ErrorCode.FILE_SIZE_EXCEEDED);
        }
    }

    private void validateImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ImageUploaderException(ErrorCode.INVALID_FILE_TYPE);
        }
    }
}
