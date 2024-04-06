package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.ModifyInfo;
import com.poppin.poppinserver.dto.managerInform.request.CreateManagerInformDto;
import com.poppin.poppinserver.dto.modifyInfo.request.CreateModifyInfoDto;
import com.poppin.poppinserver.dto.modifyInfo.response.ModifyInfoDto;
import com.poppin.poppinserver.dto.modifyInfo.response.ModifyInfoSummaryDto;
import com.poppin.poppinserver.repository.ModifyInformRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ModifyInfoService {
    private final ModifyInformRepository modifyInformRepository;

//    @Transactional
//    public ModifyInfoDto createModifyInfo(CreateModifyInfoDto createModifyInfoDto, //운영자 제보 생성
//                                          List<MultipartFile> images,
//                                          Long userId){
//
//    }

    public List<ModifyInfoSummaryDto> readModifyInfoList(){
        List<ModifyInfo> modifyInfoList = modifyInformRepository.findAll();

        return ModifyInfoSummaryDto.fromEntityList(modifyInfoList);
    }// 목록 조회
}
