package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.*;
import com.poppin.poppinserver.dto.managerInform.request.UpdateManagerInfromDto;
import com.poppin.poppinserver.dto.notification.request.PushRequestDto;
import com.poppin.poppinserver.dto.popup.request.CreatePopupDto;
import com.poppin.poppinserver.dto.popup.request.CreatePreferedDto;
import com.poppin.poppinserver.dto.popup.request.CreateTasteDto;
import com.poppin.poppinserver.dto.popup.request.UpdatePopupDto;
import com.poppin.poppinserver.dto.popup.response.*;
import com.poppin.poppinserver.dto.review.response.ReviewInfoDto;
import com.poppin.poppinserver.dto.visitorData.response.VisitorDataInfoDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.*;
import com.poppin.poppinserver.specification.PopupSpecification;
import com.poppin.poppinserver.type.EInformProgress;
import com.poppin.poppinserver.type.EUserRole;
import com.poppin.poppinserver.type.EPopupTopic;
import com.poppin.poppinserver.util.SelectRandomUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

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
    private final InterestRepository interestRepository;
    private final NotificationTokenRepository notificationTokenRepository;
    private final ReopenDemandUserRepository reopenDemandUserRepository;
    private final AlarmKeywordRepository alarmKeywordRepository;

    private final S3Service s3Service;
    private final VisitorDataService visitorDataService;
    private final VisitService visitService;
    private final NotificationService notificationService;

    private final SelectRandomUtil selectRandomUtil;

    @Transactional
    public PopupDto createPopup(CreatePopupDto createPopupDto, List<MultipartFile> images, Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

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
                .fasionBeauty(createTasteDto.fashionBeauty())
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
                .alchol(createTasteDto.alcohol())
                .animalPlant(createTasteDto.animalPlant())
                .build();

        //각 카테고리 저장
        preferedPopup = preferedPopupRepository.save(preferedPopup);
        tastePopup = tastePopupRepository.save(tastePopup);

        //날짜 요청 유효성 검증
        if (createPopupDto.openDate().isAfter(createPopupDto.closeDate())) {
            throw new CommonException(ErrorCode.INVALID_DATE_PARAMETER);
        }

        //현재 운영상태 정의
        String operationStatus;
        if (createPopupDto.openDate().isAfter(LocalDate.now())){
            Period period = Period.between(LocalDate.now(), createPopupDto.openDate());
            operationStatus = "D-" + period.getDays();
        } else if (createPopupDto.closeDate().isBefore(LocalDate.now())) {
            operationStatus = "TERMINATED";
        }
        else{
            operationStatus = "OPERATING";
        }

        // 팝업 스토어 정보 저장
        Popup popup = Popup.builder()
                .homepageLink(createPopupDto.homepageLink())
                .name(createPopupDto.name())
                .availableAge(createPopupDto.availableAge())
                .closeDate(createPopupDto.closeDate())
                .closeTime(createPopupDto.closeTime())
                .entranceFee(createPopupDto.entranceFee())
                .resvRequired(createPopupDto.resvRequired())
                .introduce(createPopupDto.introduce())
                .address(createPopupDto.address())
                .addressDetail(createPopupDto.addressDetail())
                .openDate(createPopupDto.openDate())
                .openTime(createPopupDto.openTime())
                .operationExcept(createPopupDto.operationExcept())
                .operationStatus(operationStatus)
                .parkingAvailable(createPopupDto.parkingAvailable())
                .preferedPopup(preferedPopup)
                .tastePopup(tastePopup)
                .build();

        popup.updateAgent(admin);

        popup = popupRepository.save(popup);
        log.info(popup.toString());

        // 팝업 이미지 처리 및 저장
        List<String> fileUrls = s3Service.uploadPopupPoster(images, popup.getId());

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
    } // 전체 팝업 관리 - 팝업 생성

    public PopupDto readPopup(Long adminId, Long popupId){
        // 팝업 정보 불러오기
        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        return PopupDto.fromEntity(popup);
    } // 전체 팝업 관리 - 팝업 조회

    public List<ManageSummaryDto> readManageList(Long adminId, int page, int size){
        List<Popup> popups = popupRepository.findByOperationStatusAndOrderByName(PageRequest.of(page, size));

        return ManageSummaryDto.fromEntityList(popups);
    } // 전체 팝업 관리 - 전체 팝업 조회

    public Boolean removePopup(Long popupId) {
        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        // 카테고리 삭제

        // 팝업 이미지 삭제

        // 알람 키워드 삭제

        //

        popupRepository.delete(popup);

        return true;
    } // 전체 팝업 관리 - 팝업 삭제

    @Transactional
    public PopupDto updatePopup(UpdatePopupDto updatePopupDto,
                                List<MultipartFile> images,
                                Long adminId){
        Popup popup = popupRepository.findById(updatePopupDto.popupId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        CreateTasteDto createTasteDto = updatePopupDto.taste();
        TastePopup tastePopup = popup.getTastePopup();
        tastePopup.update(createTasteDto.fashionBeauty(),
                createTasteDto.characters(),
                createTasteDto.foodBeverage(),
                createTasteDto.webtoonAni(),
                createTasteDto.interiorThings(),
                createTasteDto.movie(),
                createTasteDto.musical(),
                createTasteDto.sports(),
                createTasteDto.game(),
                createTasteDto.itTech(),
                createTasteDto.kpop(),
                createTasteDto.alcohol(),
                createTasteDto.animalPlant());
        tastePopupRepository.save(tastePopup);

        CreatePreferedDto createPreferedDto = updatePopupDto.prefered();
        PreferedPopup preferedPopup = popup.getPreferedPopup();
        preferedPopup.update(createPreferedDto.market(),
                createPreferedDto.display(),
                createPreferedDto.experience(),
                createPreferedDto.wantFree());
        preferedPopupRepository.save(preferedPopup);

        // 기존 이미지 싹 지우기
        List<PosterImage> originImages = posterImageRepository.findByPopupId(popup);
        List<String> originUrls = originImages.stream()
                .map(PosterImage::getPosterUrl)
                .collect(Collectors.toList());
        s3Service.deleteMultipleImages(originUrls);
        posterImageRepository.deleteAllByPopupId(popup);

        //새로운 이미지 추가
        List<String> fileUrls = s3Service.uploadPopupPoster(images, popup.getId());

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

        // 기존 키워드 삭제 및 다시 저장
        alarmKeywordRepository.deleteAll(popup.getAlarmKeywords());

        List<AlarmKeyword> alarmKeywords = new ArrayList<>();
        for(String keyword : updatePopupDto.keywords()){
            alarmKeywords.add(AlarmKeyword.builder()
                    .popupId(popup)
                    .keyword(keyword)
                    .build());
        }
        alarmKeywordRepository.saveAll(alarmKeywords);

        popup.update(
                updatePopupDto.homepageLink(),
                updatePopupDto.name(),
                updatePopupDto.introduce(),
                updatePopupDto.address(),
                updatePopupDto.addressDetail(),
                updatePopupDto.entranceFee(),
                updatePopupDto.resvRequired(),
                updatePopupDto.availableAge(),
                updatePopupDto.parkingAvailable(),
                updatePopupDto.openDate(),
                updatePopupDto.closeDate(),
                updatePopupDto.openTime(),
                updatePopupDto.closeTime(),
                updatePopupDto.operationExcept(),
                popup.getOperationStatus(),
                admin
        );

        popupRepository.save(popup);

        return PopupDto.fromEntity(popup);
    } // 전체 팝업 관리 - 팝업 수정

    public PopupGuestDetailDto readGuestDetail(Long popupId){
        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        popup.addViewCnt(); // 조회수 + 1

        List<Review> reviews = reviewRepository.findAllByPopupIdOrderByRecommendCntDesc(popupId, PageRequest.of(0,3)); // 후기 추천수 상위 3개

        List<ReviewInfoDto> reviewInfoList = ReviewInfoDto.fromEntityList(reviews, 0);

        VisitorDataInfoDto visitorDataDto = visitorDataService.getVisitorData(popupId); // 방문자 데이터

        Optional<Integer> visitors = visitService.showRealTimeVisitors(popupId); // 실시간 방문자

        popupRepository.save(popup);

        // 이미지 목록 가져오기
        List<PosterImage> posterImages  = posterImageRepository.findByPopupId(popup);

        List<String> imageList = new ArrayList<>();
        for(PosterImage posterImage : posterImages){
            imageList.add(posterImage.getPosterUrl());
        }

        return PopupGuestDetailDto.fromEntity(popup, imageList, reviewInfoList, visitorDataDto, visitors);
    }

    public PopupDetailDto readDetail(Long popupId, Long userId){
        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        popup.addViewCnt(); // 조회수 + 1

        List<Review> reviews = reviewRepository.findAllByPopupIdOrderByRecommendCntDesc(popupId, PageRequest.of(0,3)); // 후기 추천수 상위 3개

        List<ReviewInfoDto> reviewInfoList = ReviewInfoDto.fromEntityList(reviews, 0);

        VisitorDataInfoDto visitorDataDto = visitorDataService.getVisitorData(popupId); // 방문자 데이터

        Optional<Integer> visitors = visitService.showRealTimeVisitors(popupId); // 실시간 방문자

        popupRepository.save(popup);

        // 이미지 목록 가져오기
        List<PosterImage> posterImages  = posterImageRepository.findByPopupId(popup);

        List<String> imageList = new ArrayList<>();
        for(PosterImage posterImage : posterImages){
            imageList.add(posterImage.getPosterUrl());
        }

        // 관심 여부 확인
        Boolean isInterested = interestRepository.findByUserIdAndPopupId(userId, popupId).isPresent();

        return PopupDetailDto.fromEntity(popup, imageList, isInterested, reviewInfoList, visitorDataDto, visitors);
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

    @Transactional
    public PopupTasteDto readTasteList(Long userId){
        //유저가 선택한 카테고리 중 랜덤으로 하나 선택
        //선택된 카테고리로 리스트 생성
        //랜덤함수에서 선택 리스트 만큼 수 추출
        //고른 카테고리 기반이 true인 팝업 긁어오기

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        //취향설정이 되지 않은 유저의 경우
        if(user.getTastePopup() == null || user.getPreferedPopup() == null || user.getWhoWithPopup() == null){
            return null;
        }

        Random random = new Random();
        Integer randomIndex = random.nextInt(17);

        log.info(randomIndex.toString());

        if (randomIndex > 4){
            // Taste
            TastePopup tastePopup = user.getTastePopup();
            String selectedTaste = selectRandomUtil.selectRandomTaste(tastePopup);
            log.info("taste"+selectedTaste);

            Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "viewCnt"));
            Specification<Popup> combinedSpec = Specification.where(PopupSpecification.hasTaste(selectedTaste, true))
                    .and(PopupSpecification.isOperating());

            log.info(combinedSpec.toString());

            List<Popup> popups = popupRepository.findAll(combinedSpec, pageable).getContent();

            return new PopupTasteDto(selectedTaste, PopupSummaryDto.fromEntityList(popups));
        }
        else{
            // Prefered
            PreferedPopup preferedPopup = user.getPreferedPopup();
            String selectedPreference = selectRandomUtil.selectRandomPreference(preferedPopup);
            log.info(selectedPreference);

            Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "viewCnt"));
            Specification<Popup> combinedSpec = Specification.where(PopupSpecification.hasPrefered(selectedPreference, true))
                    .and(PopupSpecification.isOperating());


            List<Popup> popups = popupRepository.findAll(combinedSpec, pageable).getContent();

            return new PopupTasteDto(selectedPreference, PopupSummaryDto.fromEntityList(popups));
        }
    }

    public List<PopupSearchingDto> readSearchingList(String text, int page, int size, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        String[] words = text.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append(word);
            sb.append("* ");
        }
        String

        List<Popup> popups = popupRepository.findByTextInNameOrIntroduce(text, PageRequest.of(page, size));

        return PopupSearchingDto.fromEntityList(popups, user);
    } // 로그인 팝업 검색

    public List<PopupGuestSearchingDto> readGuestSearchingList(String text, int page, int size){
        List<Popup> popups = popupRepository.findByTextInNameOrIntroduce(text, PageRequest.of(page, size));

        return PopupGuestSearchingDto.fromEntityList(popups);
    }

    public String reopenDemand(Long userId, PushRequestDto pushRequestDto){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        Popup popup = popupRepository.findById(pushRequestDto.popupId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        NotificationToken token = notificationTokenRepository.findByToken(pushRequestDto.token());
        if (token==null) throw new CommonException(ErrorCode.NOT_FOUND_TOKEN);

        ReopenDemandUser reopenDemandUser = new ReopenDemandUser(user, popup, pushRequestDto.token(), token.getMod_dtm(),token.getExp_dtm());
        reopenDemandUserRepository.save(reopenDemandUser);

        popup.addreopenDemandCnt(); // 재오픈 수요 + 1
        popupRepository.save(popup);

        /* 재오픈 체크 시 재오픈 토픽에 등록 */
        log.info("==== 재오픈 수요 체크 시 FCM 관심팝업 TOPIC 등록 ====");

        notificationService.fcmAddTopic(pushRequestDto.token(), popup, EPopupTopic.REOPEN.getTopicType());


        return "재오픈 수요 체크 되었습니다.";
    }
}
