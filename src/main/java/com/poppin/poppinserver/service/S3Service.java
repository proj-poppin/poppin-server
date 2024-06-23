package com.poppin.poppinserver.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.poppin.poppinserver.domain.InformAlarmImage;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.InformAlarmImageRepository;
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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final InformAlarmImageRepository informAlarmImageRepository;
    private final AmazonS3Client s3Client;
    private final ImageUtil imageUtil;

    @Value("${cloud.aws.s3.popup-poster}")
    private String bucketPopupPoster;

    @Value("${cloud.aws.s3.inform-poster}")
    private String bucketInformPoster;

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

        for (MultipartFile file : multipartFile) {
            String fileName = createFileName(file.getOriginalFilename(), popupId);
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
                s3Client.putObject(new PutObjectRequest(bucketPopupPoster, fileName, new ByteArrayInputStream(imageBytes), objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
                imgUrlList.add(s3Client.getUrl(bucketPopupPoster, fileName).toString());
            } catch(IOException e) {
                log.error("Error processing image for modifyInfoId: " + popupId + ", fileName: " + fileName, e);
                throw new CommonException(ErrorCode.SERVER_ERROR);
            }
        }
        return imgUrlList;
    }

    // 리뷰 이미지 업로드
    public List<String> uploadReviewImage(List<MultipartFile> multipartFile, Long reviewId) {
        List<String> imgUrlList = new ArrayList<>();
        log.info("upload images");

        for (MultipartFile file : multipartFile) {
            String fileName = createFileName(file.getOriginalFilename(), reviewId);
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
                s3Client.putObject(new PutObjectRequest(bucketReviewImage, fileName, new ByteArrayInputStream(imageBytes), objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
                imgUrlList.add(s3Client.getUrl(bucketReviewImage, fileName).toString());
            } catch(IOException e) {
                log.error("Error processing image for modifyInfoId: " + reviewId + ", fileName: " + fileName, e);
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
        String imageUrl = "";
        String fileName = createFileName(multipartFile.getOriginalFilename(), userId);
        ObjectMetadata objectMetadata = new ObjectMetadata();

        try (InputStream originalInputStream = multipartFile.getInputStream()) {
            // ImageUtil을 사용하여 이미지를 1:1 비율로 조정
            InputStream processedInputStream = imageUtil.cropImageToSquare(originalInputStream);

            // 조정된 이미지의 크기를 계산
            byte[] imageBytes = processedInputStream.readAllBytes();
            objectMetadata.setContentLength(imageBytes.length);
            objectMetadata.setContentType(multipartFile.getContentType());

            // 조정된 이미지를 S3에 업로드
            s3Client.putObject(new PutObjectRequest(bucketUserProfile, fileName, new ByteArrayInputStream(imageBytes), objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            imageUrl = s3Client.getUrl(bucketUserProfile, fileName).toString();
        } catch (IOException e) {
            log.error("Error processing image for modifyInfoId: " + userId + ", fileName: " + fileName, e);
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

    // s3 이미지 다른 폴더로 복사 (기존 이미지 url과 popupId)
    public String copyImageToAnotherFolder(String url, Long popupId) {
        // URL에서 버킷 이름과 키 추출
        String sourceBucket = url.split("://")[1].split("\\.")[0];
        String sourceKey = url.split("://")[1].split("/", 2)[1];

        // 대상 버킷과 대상 키 설정 (여기에서는 동일 버킷 내 다른 폴더로 복사)
        String destinationBucket = sourceBucket; // 같은 버킷 내에서 복사
        String destinationKey = popupId + "/" + sourceKey.substring(sourceKey.lastIndexOf("/") + 1); // 새로운 폴더에 저장

        try {
            // 복사 요청 생성 및 실행
            CopyObjectRequest copyObjectRequest = new CopyObjectRequest()
                    .withSourceBucketName(sourceBucket)
                    .withSourceKey(sourceKey)
                    .withDestinationBucketName(destinationBucket)
                    .withDestinationKey(destinationKey);

            CopyObjectResult copyObjectResponse = s3Client.copyObject(copyObjectRequest);
            log.info("Copied image to new folder: {}", destinationKey);

            // 복사된 이미지의 URL 반환
            return s3Client.getResourceUrl(destinationBucket, destinationKey);
        } catch (Exception e) {
            log.error("Failed to copy image: {}", e.getMessage());
            throw new RuntimeException("Failed to copy image", e);
        }
    }

    // s3 이미지를 리스트 단위로 다른 폴더로 복사 (복사할 이미지 url List, popupId)
    public List<String> copyImageListToAnotherFolder(List<String> urls, Long popupId) {
        List<String> newUrls = new ArrayList<>();

        log.info(urls.toString());

        for (String url : urls) {
            // URL에서 버킷 이름과 키 추출
            String sourceBucket = url.split("://")[1].split("\\.")[0];
            String sourceKey = url.split("://")[1].split("/", 2)[1];

            // 대상 버킷과 대상 키 설정 (동일 버킷 내 다른 폴더로 복사)
            String destinationBucket = sourceBucket; // 같은 버킷 내에서 복사
            String destinationKey = popupId + "/" + sourceKey.substring(sourceKey.lastIndexOf("/") + 1); // 새로운 폴더에 저장

            try {
                // 복사 요청 생성 및 실행
                CopyObjectRequest copyObjectRequest = new CopyObjectRequest()
                        .withSourceBucketName(sourceBucket)
                        .withSourceKey(sourceKey)
                        .withDestinationBucketName(destinationBucket)
                        .withDestinationKey(destinationKey);

                log.info("sourceKey : {}", sourceKey);
                log.info("destKdy : {}", destinationKey);
                CopyObjectResult copyObjectResponse = s3Client.copyObject(copyObjectRequest);
                log.info("Copied image to new folder: {}", destinationKey);

                // 복사된 이미지의 URL 반환
                String newUrl = s3Client.getResourceUrl(destinationBucket, destinationKey);
                newUrls.add(newUrl);
            } catch (Exception e) {
                log.error("Failed to copy image: {}", e.getMessage());
                throw new RuntimeException("Failed to copy image", e);
            }
        }

        return newUrls;
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

    // (여러장)s3 사진 삭제 url만 주면 버킷 인식해서 알아서 지울 수 있다
    public void deleteMultipleImages(List<String> urls) {
        try {
            // 버킷 이름 추출 (모든 URL이 동일한 버킷에 있다고 가정)
            String bucketName = urls.get(0).split("://")[1].split("\\.")[0];

            // 삭제할 객체 목록 생성
            DeleteObjectsRequest multiObjectDeleteRequest = new DeleteObjectsRequest(bucketName)
                    .withKeys(urls.stream()
                            .map(url -> url.split("://")[1].split("/", 2)[1])
                            .toArray(String[]::new))
                    .withQuiet(false);

            // 객체 삭제 실행
            DeleteObjectsResult delObjRes = s3Client.deleteObjects(multiObjectDeleteRequest);
            log.info("Deleted images: {}", delObjRes.getDeletedObjects());

        } catch (AmazonServiceException e) {
            log.error("S3 Error during multi deletion: {}", e.getMessage());
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
        fileValidate.add(".tiff");
        fileValidate.add(".TIFF");
        fileValidate.add(".svg");
        fileValidate.add(".SVG");
        fileValidate.add(".WebP");
        fileValidate.add(".WEBP");
        fileValidate.add(".jfif");
        fileValidate.add(".JFIF");
        String idxFileName = fileName.substring(fileName.lastIndexOf("."));
        if (!fileValidate.contains(idxFileName)) {
            throw new CommonException(ErrorCode.UNSUPPORTED_MEDIA_TYPE);
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    // 공지사항 이미지 저장
    public List<String> uploadInformationPoster(List<MultipartFile> multipartFile) {
        List<String> imgUrlList = new ArrayList<>();
        log.info("upload images");

        for (MultipartFile file : multipartFile) {

            // seq 반환하기
            Long seq;
            Optional<InformAlarmImage> alarmImage = informAlarmImageRepository.findAlarmImageOrOrderByIdDesc();
            if (alarmImage.isEmpty()) seq = Long.valueOf(1);
            else{seq = alarmImage.get().getId()+1;}

            String fileName = createFileName(file.getOriginalFilename(), seq);
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
                s3Client.putObject(new PutObjectRequest(bucketInformPoster, fileName, new ByteArrayInputStream(imageBytes), objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
                imgUrlList.add(s3Client.getUrl(bucketInformPoster, fileName).toString());
            } catch(IOException e) {
                log.error("Error processing image for modifyInfoId: " + seq + ", fileName: " + fileName, e);
                throw new CommonException(ErrorCode.SERVER_ERROR);
            }
        }
        return imgUrlList;
    }

}
