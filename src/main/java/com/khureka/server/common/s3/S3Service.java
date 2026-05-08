package com.khureka.server.common.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * S3로 파일을 업로드한다.
     * @param multipartFile 업로드할 파일
     * @param dirName 버킷 내 디렉토리 경로 (예: "events")
     * @return S3에 업로드된 파일의 전체 URL
     */
    public String upload(MultipartFile multipartFile, String dirName) {
        String fileName = dirName + "/" + UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
        
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata));
        } catch (IOException e) {
            log.error("S3 파일 업로드 실패: {}", e.getMessage());
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.");
        }

        return amazonS3.getUrl(bucket, fileName).toString();
    }

    /**
     * S3에서 파일을 삭제한다.
     * @param fileUrl 삭제할 파일의 URL
     */
    public void delete(String fileUrl) {
        try {
            // URL에서 파일 키 추출 (예: events/uuid_name.jpg)
            String key = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            String dir = fileUrl.substring(0, fileUrl.lastIndexOf("/"));
            String dirName = dir.substring(dir.lastIndexOf("/") + 1);
            String fullKey = dirName + "/" + key;
            
            amazonS3.deleteObject(bucket, fullKey);
            log.info("S3 파일 삭제 성공: {}", fullKey);
        } catch (Exception e) {
            log.error("S3 파일 삭제 실패: {}", e.getMessage());
        }
    }
}
