package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.*;
import com.poppin.poppinserver.dto.RealTimeVisit.request.AddVisitorsDto;
import com.poppin.poppinserver.dto.popup.request.CreatePopupDto;
import com.poppin.poppinserver.dto.popup.request.CreatePreferedDto;
import com.poppin.poppinserver.dto.popup.request.CreateTasteDto;
import com.poppin.poppinserver.dto.popup.request.CreateWhoWithDto;
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
import java.time.Period;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PopupService {
    private final PopupRepository popupRepository;
    private final ReviewRepository reviewRepository;
    private final PosterImageRepository posterImageRepository;
    private final UserRepository userRepository;
    private final PreferedPopupRepository preferedPopupRepository;
    private final TastePopupRepository tastePopupRepository;
    private final WhoWithPopupRepository whoWithPopupRepository;

    private final S3Service s3Service;
    private final VisitorDataService visitorDataService;
    private final RealTimeVisitService realTimeVisitService;

    public PopupDto createPopup(CreatePopupDto createPopupDto, List<MultipartFile> images) {

        //현재 운영상태 정의
        String operationStatus;
        LocalDateTime openDateTime = createPopupDto.openDate().atTime(createPopupDto.openTime());
        LocalDateTime closeDateTime = createPopupDto.closeDate().atTime(createPopupDto.closeTime());
        if (openDateTime.isAfter(LocalDateTime.now())){
            //만약에 운영시간 기준으로 운영전이지만 오늘이 오픈날이면 일단 D-0으로 표시
            Period period = Period.between(LocalDate.now(), createPopupDto.openDate());
            operationStatus = "D-" + period.getDays();
        } else if (closeDateTime.isBefore(LocalDateTime.now())) {
            operationStatus = "TERMINATED";
        }
        else{
            operationStatus = "OPERATING";
        }

        //카테고리별 엔티티 정의
        CreatePreferedDto createPreferedDto = createPopupDto.prefered();
        PreferedPopup preferedPopup = PreferedPopup.builder()
                .market(createPreferedDto.market())
                .display(createPreferedDto.display())
                .experience(createPreferedDto.experience())
                .wantFree(createPreferedDto.wantFree())
                .build();

        CreateTasteDto createTasteDto = createPopupDto.taste();
        TastePopup tastePopup = TastePopup.builder()
                .fasionBeauty(createTasteDto.fasionBeauty())
                .characters(createTasteDto.characters())
                .foodBeverage(createTasteDto.foodBeverage())
                .webtoonAni(createTasteDto.webtoonAni())
                .interiorThings(createTasteDto.interiorThings())
                .movie(createTasteDto.movie())
                .musical(createTasteDto.musical())
                .sports(createTasteDto.sports())
                .game(createTasteDto.game())
                .itTech(createTasteDto.itTech())
                .kpop(createTasteDto.kpop())
                .alchol(createTasteDto.alchol())
                .animalPlant(createTasteDto.animalPlant())
                .build();

        CreateWhoWithDto createWhoWithDto = createPopupDto.whoWith();
        WhoWithPopup whoWithPopup = WhoWithPopup.builder()
                .solo(createWhoWithDto.solo())
                .withLover(createWhoWithDto.withLover())
                .withFamily(createWhoWithDto.withFamily())
                .withFriend(createWhoWithDto.withFriend())
                .build();

        //각 카테고리 저장
        preferedPopup = preferedPopupRepository.save(preferedPopup);
        tastePopup = tastePopupRepository.save(tastePopup);
        whoWithPopup = whoWithPopupRepository.save(whoWithPopup);

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
                .operationStatus(operationStatus)
                .parkingAvailable(createPopupDto.parkingAvailable())
                .preferedPopup(preferedPopup)
                .tastePopup(tastePopup)
                .whoWithPopup(whoWithPopup)
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

    public PopupDetailDto readDetail(Long userId, Long popupId){
        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        List<Review> reviews = reviewRepository.findAllByPopupIdOrderByRecommendCntDesc(popupId, PageRequest.of(0,3));

        List<ReviewInfoDto> reviewInfoList = ReviewInfoDto.fromEntityList(reviews, 0);

        VisitorDataInfoDto visitorDataDto = visitorDataService.getVisitorData(popupId); // 방문자 데이터

        AddVisitorsDto addVisitorsDto = new AddVisitorsDto(userId, popupId);

        Optional<Integer> visitors = realTimeVisitService.showRealTimeVisitors(addVisitorsDto); // 실시간 방문자

        return PopupDetailDto.fromEntity(popup, reviewInfoList, visitorDataDto, visitors);
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

        Set<Interest> interestes = user.getInterestes();

        return InterestedPopupDto.fromEntityList(interestes);
    }

//    public List<PopupSummaryDto> readTasteList(Long userId){
//        //유저가 선택한 카테고리 중 랜덤으로 하나 선택
//        //선택된 카테고리로 리스트 생성
//        //랜덤함수에서 선택 리스트 만큼 수 추출
//        //고른 카테고리 기반이 true인 팝업 긁어오기
//
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
//
//        //취향설정이 되지 않은 유저의 경우
//        if(user.getTastePopup() == null || user.getPreferedPopup() == null || user.getWhoWithPopup() == null){
//            return null;
//        }
//
//        List<String> tasteList = new ArrayList<>();
//        for(boolean taste : user.getTastePopup().)
//        Random random = new Random();
//
//    }

    public List<PopupSearchingDto> readSearchingList(String text, int page, int size, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        List<Popup> popups = popupRepository.findByTextInNameOrIntroduce(text, PageRequest.of(page, size));

        return PopupSearchingDto.fromEntityList(popups, user);
    }

    public List<PopupGuestSearchingDto> readGuestSearchingList(String text, int page, int size){
        List<Popup> popups = popupRepository.findByTextInNameOrIntroduce(text, PageRequest.of(page, size));

        return PopupGuestSearchingDto.fromEntityList(popups);
    }
}
