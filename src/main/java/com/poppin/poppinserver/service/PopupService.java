package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.*;
import com.poppin.poppinserver.dto.popup.request.CreatePopupDto;
import com.poppin.poppinserver.dto.popup.response.*;
import com.poppin.poppinserver.dto.review.response.ReviewInfoDto;
import com.poppin.poppinserver.dto.visitorData.common.Satisfaction;
import com.poppin.poppinserver.dto.visitorData.common.VisitDate;
import com.poppin.poppinserver.dto.visitorData.response.VisitorDataInfoDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PopupService {
    private final PopupRepository popupRepository;

    private final ReviewRepository reviewRepository;

    private final VisitorDataRepository visitorDataRepository;
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

        log.info(popup.toString());

        // 팝업 이미지 처리 및 저장
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

        popup = popupRepository.save(popup);

        return PopupDto.fromEntity(popup);
    }

    public PopupDetailDto readDetail(Long popupId){
        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        List<Review> reviews = reviewRepository.findAllByPopupIdOrderByRecommendCntDesc(popupId, PageRequest.of(0,3));

        List<ReviewInfoDto> reviewInfoList = ReviewInfoDto.fromEntityList(reviews, 0);

        Map<String,Object> weekdayAm = visitorDataRepository.findCongestionRatioByPopupId(popupId, VisitDate.fromValue("평일 오전").toString());
        Map<String,Object> weekdayPm = visitorDataRepository.findCongestionRatioByPopupId(popupId, VisitDate.fromValue("평일 오후").toString());
        Map<String,Object> weekendAm = visitorDataRepository.findCongestionRatioByPopupId(popupId, VisitDate.fromValue("주말 오전").toString());
        Map<String,Object> weekendPm = visitorDataRepository.findCongestionRatioByPopupId(popupId, VisitDate.fromValue("주말 오후").toString());
        int satisfaction = visitorDataRepository.satisfactionRate(popupId, Satisfaction.fromValue("만족").toString());

        VisitorDataInfoDto visitorDataDto = VisitorDataInfoDto.fromEntity(weekdayAm, weekdayPm, weekendAm, weekendPm, satisfaction);
        return PopupDetailDto.fromEntity(popup, reviewInfoList, visitorDataDto);
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

    @Transactional
    public List<InterestedPopupDto> readInterestedPopups(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        Set<Intereste> interestes = user.getInterestes();

        return InterestedPopupDto.fromEntityList(interestes);
    }

    public List<PopupSearchingDto> readSearchingList(String text, int page, int size){
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        List<Popup> popups = popupRepository.findByTextInNameOrIntroduce(text, PageRequest.of(page, size)).toList();

        return PopupSearchingDto.fromEntityList(popups, user);
    }
}
