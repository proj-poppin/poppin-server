package com.poppin.poppinserver.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3Client s3Client;
    private final ImageUtil imageUtil;

    @Value("${cloud.aws.s3.popup-poster}")
    private String bucketPopupPoster;

    @Value("${cloud.aws.s3.review-image}")
    private String bucketReviewImage;

    @Value("${cloud.aws.s3.user-profile}")
    private String bucketUserProfile;

    @Value("${cloud.aws.s3.modify-info}")
    private String bucketModifyInfo;

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

    //
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

    // 정보수정요청 이미지 업로드
    public List<String> uploadModifyInfo(List<MultipartFile> multipartFile, Long modifyInfoId) {
        List<String> imgUrlList = new ArrayList<>();
        log.info("upload images");

        for (MultipartFile file : multipartFile) {
            String fileName = createFileName(file.getOriginalFilename(), modifyInfoId);
            ObjectMetadata objectMetadata = new ObjectMetadata();

            try (InputStream originalInputStream = file.getInputStream()) {
                // ImageUtil을 사용하여 이미지를 1:1 비율로 조정
                InputStream processedInputStream = imageUtil.cropImageToSquare(originalInputStream);
                log.info("crop image");

                // 조정된 이미지의 크기를 계산
                byte[] imageBytes = processedInputStream.readAllBytes();
                objectMetadata.setContentLength(imageBytes.length);
                objectMetadata.setContentType(file.getContentType());

                // 조정된 이미지를 S3에 업로드
                s3Client.putObject(new PutObjectRequest(bucketModifyInfo, fileName, new ByteArrayInputStream(imageBytes), objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
                imgUrlList.add(s3Client.getUrl(bucketModifyInfo, fileName).toString());
            } catch(IOException e) {
                log.error("Error processing image for modifyInfoId: " + modifyInfoId + ", fileName: " + fileName, e);
                throw new CommonException(ErrorCode.SERVER_ERROR);
            }
        }
        return imgUrlList;
    }


    // s3 사진 삭제 url만 주면 버킷 인식해서 알아서 지울 수 있다
    public void deleteImage(String url) {
        try {
            String bucketName = url.split("://")[1].split("\\.")[0];
            log.info(bucketName);
            String filename = url.split("://")[1].split("/", 2)[1];
            log.info(filename);
            s3Client.deleteObject(bucketName, filename);
            log.info("Deleted image {} from bucket {}", filename, bucketName);
        } catch (AmazonServiceException e) {
            log.error("S3 Error deleting : {}", e.getMessage());
            throw new CommonException(ErrorCode.SERVER_ERROR);
        }
    }

    // 이미지 수정    (기존 사진 url, 사진, 해당객체 id)
    public String replaceImage(String url, MultipartFile multipartFile, Long id) {
        //기존 이미지 삭제
        deleteImage(url);

        String bucketName = url.split("://")[1].split("\\.")[0];
        log.info(bucketName);

        // 기존 이미지와 동일한 이름 사용
        String fileName = createFileName(multipartFile.getOriginalFilename(), id);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            // 기존 파일 덮어쓰기
            s3Client.putObject(new PutObjectRequest(bucketName, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            log.info("Replaced image in bucket {}: {}", bucketName, fileName);
            return s3Client.getUrl(bucketName, fileName).toString();
        } catch (IOException e) {
            log.error("Error replacing image in bucket {}: {}", bucketName, fileName);
            throw new CommonException(ErrorCode.SERVER_ERROR);
        }
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
