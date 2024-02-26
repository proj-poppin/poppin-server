package com.poppin.poppinserver.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadImage(MultipartFile file, Long popupId){ // popupId에 해당하는 팝업의 사진들 s3에 업로드
        try{
            String fileName = file.getOriginalFilename();
            String fileUrl= "https://" + bucket + "/" + popupId +fileName;
            ObjectMetadata metadata= new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            amazonS3Client.putObject(bucket,fileName,file.getInputStream(),metadata);
            return fileUrl;
        }
        catch(IOException e) {
            throw new CommonException(ErrorCode.SERVER_ERROR);
        }
    }
}
