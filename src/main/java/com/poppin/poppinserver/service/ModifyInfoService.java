package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.*;
import com.poppin.poppinserver.dto.managerInform.request.CreateManagerInformDto;
import com.poppin.poppinserver.dto.modifyInfo.request.CreateModifyInfoDto;
import com.poppin.poppinserver.dto.modifyInfo.response.ModifyInfoDto;
import com.poppin.poppinserver.dto.modifyInfo.response.ModifyInfoSummaryDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.ModifyImageReposiroty;
import com.poppin.poppinserver.repository.ModifyInformRepository;
import com.poppin.poppinserver.repository.PopupRepository;
import com.poppin.poppinserver.repository.UserRepository;
import com.poppin.poppinserver.util.ImageUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ModifyInfoService {
    private final ModifyInformRepository modifyInformRepository;
    private final ModifyImageReposiroty modifyImageReposiroty;
    private final UserRepository userRepository;
    private final PopupRepository popupRepository;

    private final S3Service s3Service;

    @Transactional
    public ModifyInfoDto createModifyInfo(CreateModifyInfoDto createModifyInfoDto,
                                          List<MultipartFile> images,
                                          Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Popup popup = popupRepository.findById(createModifyInfoDto.popupId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        // 정보수정요청 객체 저장
        ModifyInfo modifyInfo = ModifyInfo.builder()
                .content(createModifyInfoDto.content())
                .userId(user)
                .popupId(popup)
                .build();
        modifyInformRepository.save(modifyInfo);

        // 정보수정요청 이미지 처리 및 저장
        List<String> fileUrls = s3Service.uploadModifyInfo(images, modifyInfo.getId());

        List<ModifyImages> modifyImagesList = new ArrayList<>();
        for(String url : fileUrls){
            ModifyImages modifyImage = ModifyImages.builder()
                    .modifyId(modifyInfo)
                    .imageUrl(url)
                    .build();
            modifyImagesList.add(modifyImage);
        }
        modifyImageReposiroty.saveAll(modifyImagesList);

        return ModifyInfoDto.fromEntity(modifyInfo, fileUrls);
    } // 요청 생성

    @Transactional
    public ModifyInfoDto readModifyInfo(Long modifyInfoId){
        ModifyInfo modifyInfo = modifyInformRepository.findById(modifyInfoId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MODIFY_INFO));

        List<ModifyImages> modifyImageList = modifyImageReposiroty.findByModifyId(modifyInfo);

        List<String> imageList = new ArrayList<>();
        for(ModifyImages modifyImages : modifyImageList){
            imageList.add(modifyImages.getImageUrl());
        }

        return ModifyInfoDto.fromEntity(modifyInfo, imageList);
    }

    @Transactional
    public List<ModifyInfoSummaryDto> readModifyInfoList(){
        List<ModifyInfo> modifyInfoList = modifyInformRepository.findAll();

        return ModifyInfoSummaryDto.fromEntityList(modifyInfoList);
    }// 목록 조회
}
