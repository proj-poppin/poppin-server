package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.*;
import com.poppin.poppinserver.dto.notification.request.PushRequestDto;
import com.poppin.poppinserver.dto.popup.request.CreatePopupDto;
import com.poppin.poppinserver.dto.popup.request.CreatePreferedDto;
import com.poppin.poppinserver.dto.popup.request.CreateTasteDto;
import com.poppin.poppinserver.dto.popup.response.*;
import com.poppin.poppinserver.dto.review.response.ReviewInfoDto;
import com.poppin.poppinserver.dto.visitorData.response.VisitorDataInfoDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.*;
import com.poppin.poppinserver.specification.PopupSpecification;
import com.poppin.poppinserver.type.EInformProgress;
import com.poppin.poppinserver.type.ETopicType;
import com.poppin.poppinserver.type.EUserRole;
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

    private final S3Service s3Service;
    private final VisitorDataService visitorDataService;
    private final VisitService visitService;
    private final NotificationService notificationService;

    private final SelectRandomUtil selectRandomUtil;

    @Transactional
    public PopupDto createPopup(CreatePopupDto createPopupDto, List<MultipartFile> images, Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        // 관리자인지 검증
        if (admin.getRole() != EUserRole.ADMIN){
            throw new CommonException(ErrorCode.ACCESS_DENIED_ERROR);
        }

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

        ManagerInform managerInform = ManagerInform.builder()
                .informerId(admin)
                .popupId(popup)
                .affiliation("poppin")
                .progress(EInformProgress.EXECUTED)
                .informerEmail("c68254@gmail.com").build();

        return PopupDto.fromEntity(popup);
    }

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

        List<Popup> popups = popupRepository.findByTextInNameOrIntroduce(text, PageRequest.of(page, size));

        return PopupSearchingDto.fromEntityList(popups, user);
    }

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

        notificationService.fcmAddTopic(pushRequestDto.token(), popup, ETopicType.RO);


        return "재오픈 수요 체크 되었습니다.";
    }
}
