package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.Intereste;
import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.PosterImage;
import com.poppin.poppinserver.domain.User;
import com.poppin.poppinserver.dto.popup.request.CreatePopupDto;
import com.poppin.poppinserver.dto.popup.response.InterestedPopupDto;
import com.poppin.poppinserver.dto.popup.response.PopupDto;
import com.poppin.poppinserver.dto.popup.response.PopupSummaryDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.PopupRepository;
import com.poppin.poppinserver.repository.PosterImageRepository;
import com.poppin.poppinserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PopupService {
    private final PopupRepository popupRepository;
    private final PosterImageRepository posterImageRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    public PopupDto createPopup(CreatePopupDto createPopupDto, List<MultipartFile> images) {
        // 팝업 스토어 정보 저장
        Popup popup = Popup.builder()
                .name(createPopupDto.name())
                .availableAge(createPopupDto.availableAge())
                .category(createPopupDto.category())
                .closeDate(createPopupDto.closeDate())
                .closeTime(createPopupDto.closeTime())
                .entranceFee(createPopupDto.entranceFee())
                .introduce(createPopupDto.introduce())
                .location(createPopupDto.location())
                .openDate(createPopupDto.openDate())
                .openTime(createPopupDto.openTime())
                .operationStatus(createPopupDto.operationStatus())
                .parkingAvailable(createPopupDto.parkingAvailable())
                .build();

        popup = popupRepository.save(popup);

        // 팝업 이미지 처리 및 저장
        if(images != null && !images.isEmpty()){
            List<String> fileUrls = s3Service.upload(images, popup.getId());

            List<PosterImage> posterImages = new ArrayList<>();
            for(String url : fileUrls){
                PosterImage posterImage = PosterImage.builder()
                        .posterUrl(url)
                        .popup(popup)
                        .build();
                posterImages.add(posterImage);
            }
            posterImageRepository.saveAll(posterImages);
            popup.updatePosterUrl(fileUrls.get(0));
        } else {
            popup.updatePosterUrl(null);
        }

        popup = popupRepository.save(popup);

        return PopupDto.fromEntity(popup);
    }

    public List<PopupSummaryDto> readHotList(){
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime startOfDay = yesterday.atStartOfDay();
        LocalDateTime endOfDay = yesterday.plusDays(1).atStartOfDay();

        List<Popup> popups = popupRepository.findTopOperatingPopupsByInterestAndViewCount(startOfDay, endOfDay, PageRequest.of(0, 5));

        return PopupSummaryDto.fromEntityList(popups);
    }

    public List<PopupSummaryDto> readNewList(){

        List<Popup> popups = popupRepository.findNewOpenPopupByAll(PageRequest.of(0, 5));

        return PopupSummaryDto.fromEntityList(popups);
    }

    public List<PopupSummaryDto> readClosingList(){

        List<Popup> popups = popupRepository.findClosingPopupByAll(PageRequest.of(0, 5));

        return PopupSummaryDto.fromEntityList(popups);
    }

    public List<InterestedPopupDto> readInterestedPopups(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        Set<Intereste> interestes = user.getInterestes();

        return InterestedPopupDto.fromEntityList(interestes);
    }
}
