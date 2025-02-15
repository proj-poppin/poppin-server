package com.poppin.poppinserver.admin.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.poppin.poppinserver.alarm.domain.FCMToken;
import com.poppin.poppinserver.alarm.domain.PopupAlarmKeyword;
import com.poppin.poppinserver.alarm.domain.PopupTopic;
import com.poppin.poppinserver.alarm.domain.UserAlarmKeyword;
import com.poppin.poppinserver.alarm.dto.alarm.request.AlarmKeywordCreateRequestDto;
import com.poppin.poppinserver.alarm.repository.*;
import com.poppin.poppinserver.alarm.usecase.SendAlarmCommandUseCase;
import com.poppin.poppinserver.alarm.usecase.TokenQueryUseCase;
import com.poppin.poppinserver.alarm.usecase.TopicCommandUseCase;
import com.poppin.poppinserver.core.dto.PageInfoDto;
import com.poppin.poppinserver.core.dto.PagingResponseDto;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.scheduler.FCMScheduler;
import com.poppin.poppinserver.core.type.EOperationStatus;
import com.poppin.poppinserver.core.type.EPopupTopic;
import com.poppin.poppinserver.core.type.EPushInfo;
import com.poppin.poppinserver.core.util.PrepardSearchUtil;
import com.poppin.poppinserver.inform.repository.ManagerInformRepository;
import com.poppin.poppinserver.inform.repository.UserInformRepository;
import com.poppin.poppinserver.interest.usercase.InterestCommandUseCase;
import com.poppin.poppinserver.modifyInfo.service.ModifyInfoService;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.domain.PosterImage;
import com.poppin.poppinserver.popup.domain.PreferedPopup;
import com.poppin.poppinserver.popup.domain.TastePopup;
import com.poppin.poppinserver.popup.dto.popup.request.CreatePopupDto;
import com.poppin.poppinserver.popup.dto.popup.request.CreatePreferedDto;
import com.poppin.poppinserver.popup.dto.popup.request.CreateTasteDto;
import com.poppin.poppinserver.popup.dto.popup.request.UpdatePopupDto;
import com.poppin.poppinserver.popup.dto.popup.response.AdminPopupDto;
import com.poppin.poppinserver.popup.dto.popup.response.ManageListDto;
import com.poppin.poppinserver.popup.repository.*;
import com.poppin.poppinserver.popup.service.S3Service;
import com.poppin.poppinserver.popup.usecase.PopupQueryUseCase;
import com.poppin.poppinserver.report.repository.ReportPopupRepository;
import com.poppin.poppinserver.review.domain.Review;
import com.poppin.poppinserver.review.domain.ReviewImage;
import com.poppin.poppinserver.review.repository.ReviewCommandRepository;
import com.poppin.poppinserver.review.repository.ReviewImageCommandRepository;
import com.poppin.poppinserver.review.repository.ReviewQueryRepository;
import com.poppin.poppinserver.review.repository.ReviewRecommendCommandRepository;
import com.poppin.poppinserver.review.usecase.ReviewImageQueryUseCase;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import com.poppin.poppinserver.visit.repository.VisitRepository;
import com.poppin.poppinserver.visit.repository.VisitorDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminPopupService {
    private final PopupRepository popupRepository;
    private final ReviewQueryRepository reviewRepository;
    private final ReviewCommandRepository reviewCommandRepository;
    private final PosterImageRepository posterImageRepository;
    private final PreferedPopupRepository preferedPopupRepository;
    private final TastePopupRepository tastePopupRepository;
    private final FCMTokenRepository fcmTokenRepository;
    private final PopupAlarmKeywordRepository popupAlarmKeywordRepository;
    private final ReviewImageQueryUseCase reviewImageQueryUseCase;
    private final ReviewImageCommandRepository reviewImageCommandRepository;
    private final VisitRepository visitRepository;
    private final ManagerInformRepository managerInformRepository;
    private final UserInformRepository userInformRepository;
    private final ReportPopupRepository reportPopupRepository;
    private final ReviewRecommendCommandRepository reviewRecommendRepository;
    private final PopupTopicRepository popupTopicRepository;
    private final BlockedPopupRepository blockedPopupRepository;
    private final PopupAlarmRepository popupAlarmRepository;
    private final VisitorDataRepository visitorDataRepository;
    private final UserAlarmKeywordRepository userAlarmKeywordRepository;

    private final S3Service s3Service;
    private final ModifyInfoService modifyInfoService;

    private final UserQueryUseCase userQueryUseCase;
    private final PopupQueryUseCase popupQueryUseCase;
    private final InterestCommandUseCase interestCommandUseCase;

    private final PrepardSearchUtil prepardSearchUtil;

    private final TokenQueryUseCase tokenQueryUseCase;
    private final TopicCommandUseCase topicCommandUseCase;
    private final SendAlarmCommandUseCase sendAlarmCommandUseCase;

    private final FCMScheduler fcmScheduler;

    @Transactional
    public AdminPopupDto createPopup(CreatePopupDto createPopupDto, List<MultipartFile> images, Long adminId) {
        User admin = userQueryUseCase.findUserById(adminId);

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
                .webtoonAni(createTasteDto.webtoonAnimation())
                .interiorThings(createTasteDto.interiorThings())
                .movie(createTasteDto.movie())
                .musical(createTasteDto.musical())
                .sports(createTasteDto.sports())
                .game(createTasteDto.game())
                .itTech(createTasteDto.itTech())
                .kpop(createTasteDto.kpop())
                .alcohol(createTasteDto.alcohol())
                .animalPlant(createTasteDto.animalPlant())
                .etc(createTasteDto.etc())
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
        if (createPopupDto.openDate().isAfter(LocalDate.now())) {
            operationStatus = EOperationStatus.NOTYET.getStatus();
        } else if (createPopupDto.closeDate().isBefore(LocalDate.now())) {
            operationStatus = EOperationStatus.TERMINATED.getStatus();
        } else {
            operationStatus = EOperationStatus.OPERATING.getStatus();
        }

        // 입장료 유무 false일 경우, 입장료 무료
        String entranceFee = createPopupDto.entranceFee();
        if (!createPopupDto.entranceRequired()) {
            entranceFee = "무료";
        }

        // 팝업 스토어 정보 저장
        Popup popup = Popup.builder()
                .homepageLink(createPopupDto.homepageLink())
                .name(createPopupDto.name())
                .availableAge(createPopupDto.availableAge())
                .closeDate(createPopupDto.closeDate())
                .closeTime(createPopupDto.closeTime())
                .entranceRequired(createPopupDto.entranceRequired())
                .entranceFee(entranceFee)
                .resvRequired(createPopupDto.resvRequired())
                .introduce(createPopupDto.introduce())
                .address(createPopupDto.address())
                .addressDetail(createPopupDto.addressDetail())
                .openDate(createPopupDto.openDate())
                .openTime(createPopupDto.openTime())
                .latitude(createPopupDto.latitude())
                .longitude(createPopupDto.longitude())
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
        for (String url : fileUrls) {
            PosterImage posterImage = PosterImage.builder()
                    .posterUrl(url)
                    .popup(popup)
                    .build();
            posterImages.add(posterImage);
        }
        posterImageRepository.saveAll(posterImages);
        popup.updatePosterUrl(fileUrls.get(0));

        popup = popupRepository.save(popup);

        log.info(popup.getName() + " 팝업생성");

        // 유저 알람 키워드와 매칭하여 알림 발송
        List<UserAlarmKeyword> allKeywords = userAlarmKeywordRepository.findAll();
        for (UserAlarmKeyword userAlarmKeyword : allKeywords) {
            if (userAlarmKeyword.getIsOn() && (popup.getName().contains(userAlarmKeyword.getKeyword()) ||
                    popup.getAddress().contains(userAlarmKeyword.getKeyword()) ||
                    popup.getAddressDetail().contains(userAlarmKeyword.getKeyword()))) {
                AlarmKeywordCreateRequestDto alarmKeywordCreateRequestDto = AlarmKeywordCreateRequestDto.builder()
                        .title(EPushInfo.KEYWORD_ALARM.getTitle())
                        .body(EPushInfo.KEYWORD_ALARM.getBody())
                        .build();

                // 유저에게 FCM 토큰 메시지 발송
                FCMToken token = fcmTokenRepository.findByToken(userAlarmKeyword.getFcmToken());
                sendAlarmCommandUseCase.sendKeywordAlarm(token, alarmKeywordCreateRequestDto, userAlarmKeyword);
            }
        }

        return AdminPopupDto.fromEntity(popup);
    } // 전체 팝업 관리 - 팝업 생성


    public AdminPopupDto readPopup(Long popupId) {
        // 팝업 정보 불러오기
        Popup popup = popupQueryUseCase.findPopupById(popupId);

        return AdminPopupDto.fromEntity(popup);
    } // 전체 팝업 관리 - 팝업 조회

    public PagingResponseDto<ManageListDto> readManageList(Long adminId, EOperationStatus oper, int page, int size) {
        Page<Popup> popups = popupRepository.findByOperationStatusAndOrderByName(PageRequest.of(page, size),
                oper.getStatus());

        // 각 운영상태별로 팝업 개수 반환
        Long num = switch (oper) {
            case NOTYET -> popupRepository.countByOperationStatus(EOperationStatus.NOTYET.getStatus());
            case TERMINATED -> popupRepository.countByOperationStatus(EOperationStatus.TERMINATED.getStatus());
            case OPERATING -> popupRepository.countByOperationStatus(EOperationStatus.OPERATING.getStatus());
            default -> 0L;
        };

        PageInfoDto pageInfoDto = PageInfoDto.fromPageInfo(popups);
        ManageListDto manageListDto = ManageListDto.fromEntityList(popups.getContent(), num);

        return PagingResponseDto.fromEntityAndPageInfo(manageListDto, pageInfoDto);
    } // 전체 팝업 관리 - 전체 팝업 조회

    @Transactional
    public Boolean removePopup(Long popupId) throws FirebaseMessagingException {
        Popup popup = popupQueryUseCase.findPopupById(popupId);

        // 실시간 방문자 수 관련 데이터
        log.info("delete visit data");
        visitRepository.deleteAllByPopup(popup);

        // 후기 관련 데이터
        // 후기 이미지
        log.info("delete review image");
        List<Review> reviews = reviewRepository.findByPopupId(popupId);

        for (Review review : reviews) {
            visitorDataRepository.deleteAllByReviewId(review.getId());
            List<ReviewImage> reviewImages = reviewImageQueryUseCase.findAllByReviewId(review.getId());
            List<String> reviewUrls = reviewImages.stream()
                    .map(ReviewImage::getImageUrl)
                    .toList();
            if (reviewUrls.size() != 0) {
                s3Service.deleteMultipleImages(reviewUrls);
                reviewImageCommandRepository.deleteAllByReviewId(review.getId());
            }
        }

        log.info("delete review data");

        reviewRecommendRepository.deleteAllByReviewPopup(popup);

        reviewCommandRepository.deleteAllByPopup(popup);

        // 알람 관련 데이터(1. 구독 해제, 2. topic 삭제)
        List<PopupTopic> topicList = popupTopicRepository.findByPopup(popup);

        for (PopupTopic topic : topicList) {
            User user = topic.getUser();

            FCMToken fcmToken = tokenQueryUseCase.findByUser(user);
            topicCommandUseCase.unsubscribePopupTopic(user, fcmToken, popup, EPopupTopic.MAGAM);
            topicCommandUseCase.unsubscribePopupTopic(user, fcmToken, popup, EPopupTopic.OPEN);
            topicCommandUseCase.unsubscribePopupTopic(user, fcmToken, popup, EPopupTopic.CHANGE_INFO);
        }

        // 관심 추가 데이터
        log.info("delete interest data");
        interestCommandUseCase.deleteAllInterestsByPopupId(popupId);

        // 신고 관련 데이터
        log.info("delete report data");
        reportPopupRepository.deleteAllByPopupId(popup);

        // 제보 관련 데이터
        log.info("delete inform data");
        // 운영자 제보
        managerInformRepository.deleteAllByPopupId(popup);
        // 사용자 제보
        userInformRepository.deleteAllByPopupId(popup);

        // 정보수정요청 관련 데이터
        log.info("delete modify info data");
        modifyInfoService.deleteProxyPopupAndModifyInfoByPopupId(popupId);

        // 알람 키워드
        log.info("delete alarm data");
        popupAlarmKeywordRepository.deleteAllByPopupId(popup);

        // 팝업 이미지
        log.info("delete popup image");
        List<PosterImage> posterImages = posterImageRepository.findAllByPopupId(popup);
        List<String> fileUrls = posterImages.stream()
                .map(PosterImage::getPosterUrl)
                .toList();
        if (fileUrls.size() != 0) {
            s3Service.deleteMultipleImages(fileUrls);
            posterImageRepository.deleteAllByPopupId(popup);
        }
        log.info("delete popup alarm");
        popupAlarmRepository.deleteAllByPopup(popup);

        log.info("delete popup topic");
        popupTopicRepository.deleteAllByPopup(popup);

        log.info("delete blocked popup");
        blockedPopupRepository.deleteAllByPopupId(popup);

        log.info("delete popup");
        popupRepository.delete(popup);

        return true;
    } // 전체 팝업 관리 - 팝업 삭제

    @Transactional
    public AdminPopupDto updatePopup(UpdatePopupDto updatePopupDto,
                                     List<MultipartFile> images,
                                     Long adminId) {
        Popup popup = popupQueryUseCase.findPopupById(updatePopupDto.popupId());

        User admin = userQueryUseCase.findUserById(adminId);

        CreateTasteDto createTasteDto = updatePopupDto.taste();
        TastePopup tastePopup = popup.getTastePopup();
        tastePopup.update(createTasteDto.fashionBeauty(),
                createTasteDto.characters(),
                createTasteDto.foodBeverage(),
                createTasteDto.webtoonAnimation(),
                createTasteDto.interiorThings(),
                createTasteDto.movie(),
                createTasteDto.musical(),
                createTasteDto.sports(),
                createTasteDto.game(),
                createTasteDto.itTech(),
                createTasteDto.kpop(),
                createTasteDto.alcohol(),
                createTasteDto.animalPlant(),
                createTasteDto.etc());
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
        for (String url : fileUrls) {
            PosterImage posterImage = PosterImage.builder()
                    .posterUrl(url)
                    .popup(popup)
                    .build();
            posterImages.add(posterImage);
        }
        posterImageRepository.saveAll(posterImages);
        popup.updatePosterUrl(fileUrls.get(0));

        // 기존 키워드 삭제 및 다시 저장
        popupAlarmKeywordRepository.deleteAll(popup.getPopupAlarmKeywords());

        List<PopupAlarmKeyword> popupAlarmKeywords = new ArrayList<>();
        for (String keyword : updatePopupDto.keywords()) {
            popupAlarmKeywords.add(PopupAlarmKeyword.builder()
                    .popupId(popup)
                    .keyword(keyword)
                    .build());
        }
        popupAlarmKeywordRepository.saveAll(popupAlarmKeywords);

        //날짜 요청 유효성 검증
        if (updatePopupDto.openDate().isAfter(updatePopupDto.closeDate())) {
            throw new CommonException(ErrorCode.INVALID_DATE_PARAMETER);
        }

        //현재 운영상태 정의
        String operationStatus;
        if (updatePopupDto.openDate().isAfter(LocalDate.now())) {
            operationStatus = EOperationStatus.NOTYET.getStatus();
        } else if (updatePopupDto.closeDate().isBefore(LocalDate.now())) {
            operationStatus = EOperationStatus.TERMINATED.getStatus();
        } else {
            fcmScheduler.schedulerFcmPopupTopicByType(List.of(popup), EPopupTopic.REOPEN, EPushInfo.REOPEN);
            operationStatus = EOperationStatus.OPERATING.getStatus();
        }

        // 입장료 유무 false일 경우, 입장료 무료
        String entranceFee = updatePopupDto.entranceFee();
        if (!updatePopupDto.entranceRequired()) {
            entranceFee = "무료";
        }

        popup.update(
                updatePopupDto.homepageLink(),
                updatePopupDto.name(),
                updatePopupDto.introduce(),
                updatePopupDto.address(),
                updatePopupDto.addressDetail(),
                updatePopupDto.entranceRequired(),
                entranceFee,
                updatePopupDto.resvRequired(),
                updatePopupDto.availableAge(),
                updatePopupDto.parkingAvailable(),
                updatePopupDto.openDate(),
                updatePopupDto.closeDate(),
                updatePopupDto.openTime(),
                updatePopupDto.closeTime(),
                updatePopupDto.latitude(),
                updatePopupDto.longitude(),
                updatePopupDto.operationExcept(),
                operationStatus,
                admin
        );

        popupRepository.save(popup);

        // 팝업 정보 변경 시 앱푸시 보내기
        if (!EOperationStatus.OPERATING.getStatus().equals(operationStatus)) {
            fcmScheduler.schedulerFcmPopupTopicByType(List.of(popup), EPopupTopic.CHANGE_INFO, EPushInfo.CHANGE_INFO);
        }

        return AdminPopupDto.fromEntity(popup);
    } // 전체 팝업 관리 - 팝업 수정

    public PagingResponseDto<ManageListDto> searchManageList(String text, int page, int size,
                                                             EOperationStatus oper) {
        // 검색어 토큰화 및 Full Text 와일드 카드 적용
        String searchText = null;
        if (text != null && text.trim() != "") {
            searchText = prepardSearchUtil.prepareSearchText(text);
        }

        Page<Popup> popups = popupRepository.findByTextInName(searchText, PageRequest.of(page, size),
                oper.getStatus()); // 운영 상태

        Long num = popupRepository.countByOperationStatus(oper.getStatus());

        PageInfoDto pageInfoDto = PageInfoDto.fromPageInfo(popups);
        ManageListDto manageListDto = ManageListDto.fromEntityList(popups.getContent(), num);

        return PagingResponseDto.fromEntityAndPageInfo(manageListDto, pageInfoDto);
    } // 전체 팝업 관리 - 전체 팝업 검색
}
