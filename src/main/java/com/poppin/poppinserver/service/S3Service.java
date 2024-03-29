package com.poppin.poppinserver.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3Client s3Client;

    @Value("${cloud.aws.s3.popup-poster}")
    private String bucketPopupPoster;

    @Value("${cloud.aws.s3.review-image}")
    private String bucketReviewImage;

    @Value("${cloud.aws.s3.user-profile}")
    private String bucketUserProfile;

    // 팝업 포스터 업로드
    public List<String> uploadPopupPoster(List<MultipartFile> multipartFile, Long popupId) {
        List<String> imgUrlList = new ArrayList<>();
        log.info("upload images");

        // forEach 구문을 통해 multipartFile로 넘어온 파일들 하나씩 fileNameList에 추가
        for (MultipartFile file : multipartFile) {
            String fileName = createFileName(file.getOriginalFilename(), popupId);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            try(InputStream inputStream = file.getInputStream()) {
                s3Client.putObject(new PutObjectRequest(bucketPopupPoster, fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
                imgUrlList.add(s3Client.getUrl(bucketPopupPoster, fileName).toString());
            } catch(IOException e) {
                throw new CommonException(ErrorCode.SERVER_ERROR);
            }
        }
        return imgUrlList;
    }

    // 리뷰 이미지 업로드
    public List<String> uploadReviewImage(List<MultipartFile> multipartFile, Long reviewId) {
        List<String> imgUrlList = new ArrayList<>();
        log.info("upload images");

        // forEach 구문을 통해 multipartFile로 넘어온 파일들 하나씩 fileNameList에 추가
        for (MultipartFile file : multipartFile) {
            String fileName = createFileName(file.getOriginalFilename(), reviewId);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            try(InputStream inputStream = file.getInputStream()) {
                s3Client.putObject(new PutObjectRequest(bucketReviewImage, fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
                imgUrlList.add(s3Client.getUrl(bucketReviewImage, fileName).toString());
            } catch(IOException e) {
                throw new CommonException(ErrorCode.SERVER_ERROR);
            }
        }
        return imgUrlList;
    }

    // 유저 프로필 이미지 업로드
//    public List<String> uploadUserProfile(List<MultipartFile> multipartFile, Long popupId) {
//        List<String> imgUrlList = new ArrayList<>();
//        log.info("upload images");
//
//        // forEach 구문을 통해 multipartFile로 넘어온 파일들 하나씩 fileNameList에 추가
//        for (MultipartFile file : multipartFile) {
//            String fileName = createFileName(file.getOriginalFilename(), popupId);
//            ObjectMetadata objectMetadata = new ObjectMetadata();
//            objectMetadata.setContentLength(file.getSize());
//            objectMetadata.setContentType(file.getContentType());
//
//            try(InputStream inputStream = file.getInputStream()) {
//                s3Client.putObject(new PutObjectRequest(bucketUserProfile, fileName, inputStream, objectMetadata)
//                        .withCannedAcl(CannedAccessControlList.PublicRead));
//                imgUrlList.add(s3Client.getUrl(bucketUserProfile, fileName).toString());
//            } catch(IOException e) {
//                throw new CommonException(ErrorCode.SERVER_ERROR);
//            }
//        }
//        return imgUrlList;
//    }

    public String uploadUserProfile(MultipartFile multipartFile, Long userId) {
        String imageUrl;
        String fileName = createFileName(multipartFile.getOriginalFilename(), userId);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            s3Client.putObject(new PutObjectRequest(bucketUserProfile, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            imageUrl = s3Client.getUrl(bucketUserProfile, fileName).toString();
        } catch (IOException e) {
            throw new CommonException(ErrorCode.SERVER_ERROR);
        }

        return imageUrl;
    }

    // 이미지파일명 중복 방지
    private String createFileName(String fileName, Long popupId) {
        if(fileName.isEmpty()){
            throw new CommonException(ErrorCode.MISSING_REQUEST_IMAGES);
        }
        // 파일 확장자 추출
        String extension = getFileExtension(fileName);
        // 파일 이름에서 확장자를 제외한 부분 추출
        String baseName = fileName.substring(0, fileName.lastIndexOf("."));
        // S3에 저장될 경로 구성: popupId 폴더 안에 원본 파일 이름으로 저장
        return popupId + "/" + baseName + extension;
    }

    // 파일 유효성 검사
    private String getFileExtension(String fileName) {
        ArrayList<String> fileValidate = new ArrayList<>();
        fileValidate.add(".jpg");
        fileValidate.add(".jpeg");
        fileValidate.add(".png");
        fileValidate.add(".JPG");
        fileValidate.add(".JPEG");
        fileValidate.add(".PNG");
        String idxFileName = fileName.substring(fileName.lastIndexOf("."));
        if (!fileValidate.contains(idxFileName)) {
            throw new CommonException(ErrorCode.UNSUPPORTED_MEDIA_TYPE);
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
