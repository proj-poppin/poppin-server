package com.poppin.poppinserver.modifyInfo.service;

import com.poppin.poppinserver.modifyInfo.domain.ModifyImages;
import com.poppin.poppinserver.modifyInfo.domain.ModifyInfo;
import com.poppin.poppinserver.modifyInfo.repository.ModifyImageReposiroty;
import com.poppin.poppinserver.modifyInfo.usecase.ModifyImagesCommandUseCase;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ModifyImagesCommandService implements ModifyImagesCommandUseCase {
    private final ModifyImageReposiroty modifyImageReposiroty;

    private final S3Service s3Service;

    @Override
    public List<String> saveModifyImagerList(List<MultipartFile> images, ModifyInfo modifyInfo) {
        List<String> fileUrls = s3Service.uploadModifyInfo(images, modifyInfo.getId());

        List<ModifyImages> modifyImagesList = new ArrayList<>();
        for (String url : fileUrls) {
            ModifyImages modifyImage = ModifyImages.builder()
                    .modifyId(modifyInfo)
                    .imageUrl(url)
                    .build();
            modifyImagesList.add(modifyImage);
        }
        modifyImageReposiroty.saveAll(modifyImagesList);

        return fileUrls;
    }

    @Override
    public void deleteModifyImageList(ModifyInfo modifyInfo) {
        List<ModifyImages> modifyImages = modifyImageReposiroty.findByModifyId(modifyInfo);
        List<String> modifyUrls = modifyImages.stream()
                .map(ModifyImages::getImageUrl)
                .toList();
        if (modifyUrls.size() != 0) {
            s3Service.deleteMultipleImages(modifyUrls);
            modifyImageReposiroty.deleteAllByModifyId(modifyInfo);
        }
    }
}
