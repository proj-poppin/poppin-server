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
import com.poppin.poppinserver.type.*;
import com.poppin.poppinserver.util.PrepardSearchUtil;
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
    private final ReviewImageRepository reviewImageRepository;

    private final S3Service s3Service;
    private final VisitorDataService visitorDataService;
    private final VisitService visitService;
    private final FCMService fcmService;

    private final SelectRandomUtil selectRandomUtil;
    private final PrepardSearchUtil prepardSearchUtil;

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
        EOperationStatus operationStatus;
        if (createPopupDto.openDate().isAfter(LocalDate.now())){
            operationStatus = EOperationStatus.NOTYET;
        } else if (createPopupDto.closeDate().isBefore(LocalDate.now())) {
            operationStatus = EOperationStatus.TERMINATED;
        }
        else{
            operationStatus = EOperationStatus.OPERATING;
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

    public List<ManageSearchingDto> readManageList(String text, String taste, String prepered,
                                                   EOperationStatus oper, EPopupSort order, int page, int size){
        // 팝업 형태 3개
        Boolean market = (taste.charAt(0) == '1') ? true : null;
        Boolean display = (taste.charAt(1) == '1') ? true : null;
        Boolean experience = (taste.charAt(2) == '1') ? true : null;

        // 팝업 취향 13개
        Boolean fashionBeauty = (prepered.charAt(0) == '1') ? true : null;
        Boolean characters = (prepered.charAt(1) == '1') ? true : null;
        Boolean foodBeverage = (prepered.charAt(2) == '1') ? true : null;
        Boolean webtoonAni = (prepered.charAt(3) == '1') ? true : null;
        Boolean interiorThings = (prepered.charAt(4) == '1') ? true : null;
        Boolean movie = (prepered.charAt(5) == '1') ? true : null;
        Boolean musical = (prepered.charAt(6) == '1') ? true : null;
        Boolean sports = (prepered.charAt(7) == '1') ? true : null;
        Boolean game = (prepered.charAt(8) == '1') ? true : null;
        Boolean itTech = (prepered.charAt(9) == '1') ? true : null;
        Boolean kpop = (prepered.charAt(10) == '1') ? true : null;
        Boolean alcohol = (prepered.charAt(11) == '1') ? true : null;
        Boolean animalPlant = (prepered.charAt(12) == '1') ? true : null;
        Boolean etc = (prepered.charAt(13) == '1') ? true : null;

        // 검색어 토큰화 및 Full Text 와일드 카드 적용
        String searchText = prepardSearchUtil.prepareSearchText(text);

        // order에 따른 정렬 방식 설정
        Sort sort = Sort.by("id"); // 기본 정렬은 id에 대한 정렬을 설정
        if (order != null) {
            switch (order) {
                case OPEN:
                    sort = Sort.by(Sort.Direction.DESC, "open_date");
                    break;
                case CLOSE:
                    sort = Sort.by(Sort.Direction.ASC, "close_date");
                    break;
                case VIEW:
                    sort = Sort.by(Sort.Direction.DESC, "view_cnt");
                    break;
                case UPLOAD:
                    sort = Sort.by(Sort.Direction.DESC, "created_at");
                    break;
            }
        }

        List<Popup> popups = popupRepository.findByTextInNameOrIntroduce(searchText, PageRequest.of(page, size, sort),
                market, display, experience, // 팝업 형태 3개
                fashionBeauty, characters, foodBeverage, // 팝업 취향 13개
                webtoonAni, interiorThings, movie,
                musical, sports, game,
                itTech, kpop, alcohol,
                animalPlant, etc,
                oper); // 운영 상태

        return ManageSearchingDto.fromEntityList(popups);
    } // 전체 팝업 관리 - 전체 팝업 검색

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
    } // 비로그인 상세조회

    public PopupDetailDto readDetail(Long popupId, Long userId){
        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        popup.addViewCnt(); // 조회수 + 1

        List<Review> reviews = reviewRepository.findAllByPopupIdOrderByRecommendCntDesc(popupId, PageRequest.of(0,3)); // 후기 추천수 상위 3개
        
        // 리뷰 이미지 목록 가져오기
        List<ReviewImage> reviewImages = reviewImageRepository.findAllByPopupId(popup);

        List<String> reviewImagesList = new ArrayList<>();
        for(ReviewImage reviewImage : reviewImages){
            reviewImagesList.add(reviewImage.getImageUrl());
        }

        List<ReviewInfoDto> reviewInfoList = ReviewInfoDto.fromEntityList(reviews, 0);

        VisitorDataInfoDto visitorDataDto = visitorDataService.getVisitorData(popupId); // 방문자 데이터

        Optional<Integer> visitors = visitService.showRealTimeVisitors(popupId); // 실시간 방문자

        popupRepository.save(popup);

        // 이미지 목록 가져오기
        List<PosterImage> posterImages  = posterImageRepository.findAllByPopupId(popup);

        List<String> imageList = new ArrayList<>();
        for(PosterImage posterImage : posterImages){
            imageList.add(posterImage.getPosterUrl());
        }

        // 관심 여부 확인
        Boolean isInterested = interestRepository.findByUserIdAndPopupId(userId, popupId).isPresent();

        return PopupDetailDto.fromEntity(popup, imageList, reviewImagesList, isInterested, reviewInfoList, visitorDataDto, visitors);
    } // 로그인 상세조회

    public List<PopupSummaryDto> readHotList(){
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime startOfDay = yesterday.atStartOfDay();
        LocalDateTime endOfDay = yesterday.plusDays(1).atStartOfDay();

        List<Popup> popups = popupRepository.findTopOperatingPopupsByInterestAndViewCount(startOfDay, endOfDay, PageRequest.of(0, 5));

        return PopupSummaryDto.fromEntityList(popups);
    } // 인기 팝업 조회

    public List<PopupSummaryDto> readNewList(){

        List<Popup> popups = popupRepository.findNewOpenPopupByAll(PageRequest.of(0, 5));

        return PopupSummaryDto.fromEntityList(popups);
    } // 새로 오픈 팝업 조회

    public List<PopupSummaryDto> readClosingList(){

        List<Popup> popups = popupRepository.findClosingPopupByAll(PageRequest.of(0, 5));

        return PopupSummaryDto.fromEntityList(popups);
    } // 종료 임박 팝업 조회

    @Transactional
    public List<InterestedPopupDto> readInterestedPopups(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        Set<Interest> interestes = user.getInterestes();

        return InterestedPopupDto.fromEntityList(interestes);
    } // 관심 팝업 목록 조회

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
    } // 취향저격 팝업 조회

    public List<PopupSearchingDto> readSearchingList(String text, String taste, String prepered,
                                                     EOperationStatus oper, EPopupSort order, int page, int size,
                                                     Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        // 팝업 형태 3개
        Boolean market = (taste.charAt(0) == '1') ? true : null;
        Boolean display = (taste.charAt(1) == '1') ? true : null;
        Boolean experience = (taste.charAt(2) == '1') ? true : null;

        // 팝업 취향 13개
        Boolean fashionBeauty = (prepered.charAt(0) == '1') ? true : null;
        Boolean characters = (prepered.charAt(1) == '1') ? true : null;
        Boolean foodBeverage = (prepered.charAt(2) == '1') ? true : null;
        Boolean webtoonAni = (prepered.charAt(3) == '1') ? true : null;
        Boolean interiorThings = (prepered.charAt(4) == '1') ? true : null;
        Boolean movie = (prepered.charAt(5) == '1') ? true : null;
        Boolean musical = (prepered.charAt(6) == '1') ? true : null;
        Boolean sports = (prepered.charAt(7) == '1') ? true : null;
        Boolean game = (prepered.charAt(8) == '1') ? true : null;
        Boolean itTech = (prepered.charAt(9) == '1') ? true : null;
        Boolean kpop = (prepered.charAt(10) == '1') ? true : null;
        Boolean alcohol = (prepered.charAt(11) == '1') ? true : null;
        Boolean animalPlant = (prepered.charAt(12) == '1') ? true : null;
        Boolean etc = (prepered.charAt(13) == '1') ? true : null;

        // 검색어 토큰화 및 Full Text 와일드 카드 적용
        String searchText = null;
        if (text != null && text.trim() != ""){
            searchText = prepardSearchUtil.prepareSearchText(text);
        }

        // order에 따른 정렬 방식 설정
        Sort sort = Sort.by("id"); // 기본 정렬은 id에 대한 정렬을 설정
        if (order != null) {
            switch (order) {
                case OPEN:
                    sort = Sort.by(Sort.Direction.DESC, "open_date");
                    break;
                case CLOSE:
                    sort = Sort.by(Sort.Direction.ASC, "close_date");
                    break;
                case VIEW:
                    sort = Sort.by(Sort.Direction.DESC, "view_cnt");
                    break;
                case UPLOAD:
                    sort = Sort.by(Sort.Direction.DESC, "created_at");
                    break;
            }
        }

        List<Popup> popups = popupRepository.findByTextInNameOrIntroduce(searchText, PageRequest.of(page, size, sort),
                market, display, experience, // 팝업 형태 3개
                fashionBeauty, characters, foodBeverage, // 팝업 취향 13개
                webtoonAni, interiorThings, movie,
                musical, sports, game,
                itTech, kpop, alcohol,
                animalPlant, etc,
                oper); // 운영 상태

        return PopupSearchingDto.fromEntityList(popups, user);
    } // 로그인 팝업 검색

    public List<PopupSearchingDto> readBaseList(String text, int page, int size, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        // 검색어 토큰화 및 Full Text 와일드 카드 적용
        String searchText = null;
        if (text != null && text.trim() != ""){
            searchText = prepardSearchUtil.prepareSearchText(text);
        }

        List<Popup> popups = popupRepository.findByTextInNameOrIntroduceBase(searchText, PageRequest.of(page, size)); // 운영 상태

        return PopupSearchingDto.fromEntityList(popups, user);
    } // 로그인 베이스 팝업 검색

    public List<PopupGuestSearchingDto> readGuestSearchingList(String text, String taste, String prepered,
                                                               EOperationStatus oper, EPopupSort order, int page, int size){
        // 팝업 형태 3개
        Boolean market = (taste.charAt(0) == '1') ? true : null;
        Boolean display = (taste.charAt(1) == '1') ? true : null;
        Boolean experience = (taste.charAt(2) == '1') ? true : null;

        // 팝업 취향 13개
        Boolean fashionBeauty = (prepered.charAt(0) == '1') ? true : null;
        Boolean characters = (prepered.charAt(1) == '1') ? true : null;
        Boolean foodBeverage = (prepered.charAt(2) == '1') ? true : null;
        Boolean webtoonAni = (prepered.charAt(3) == '1') ? true : null;
        Boolean interiorThings = (prepered.charAt(4) == '1') ? true : null;
        Boolean movie = (prepered.charAt(5) == '1') ? true : null;
        Boolean musical = (prepered.charAt(6) == '1') ? true : null;
        Boolean sports = (prepered.charAt(7) == '1') ? true : null;
        Boolean game = (prepered.charAt(8) == '1') ? true : null;
        Boolean itTech = (prepered.charAt(9) == '1') ? true : null;
        Boolean kpop = (prepered.charAt(10) == '1') ? true : null;
        Boolean alcohol = (prepered.charAt(11) == '1') ? true : null;
        Boolean animalPlant = (prepered.charAt(12) == '1') ? true : null;
        Boolean etc = (prepered.charAt(13) == '1') ? true : null;

        // 검색어 토큰화 및 Full Text 와일드 카드 적용
        String searchText = null;
        if (text != null && text.trim() != ""){
            searchText = prepardSearchUtil.prepareSearchText(text);
        }

        // order에 따른 정렬 방식 설정
        Sort sort = Sort.by("id"); // 기본 정렬은 id에 대한 정렬을 설정
        if (order != null) {
            switch (order) {
                case OPEN:
                    sort = Sort.by(Sort.Direction.DESC, "open_date");
                    break;
                case CLOSE:
                    sort = Sort.by(Sort.Direction.ASC, "close_date");
                    break;
                case VIEW:
                    sort = Sort.by(Sort.Direction.DESC, "view_cnt");
                    break;
                case UPLOAD:
                    sort = Sort.by(Sort.Direction.DESC, "created_at");
                    break;
            }
        }

        List<Popup> popups = popupRepository.findByTextInNameOrIntroduce(searchText, PageRequest.of(page, size, sort),
                market, display, experience, // 팝업 형태 3개
                fashionBeauty, characters, foodBeverage, // 팝업 취향 13개
                webtoonAni, interiorThings, movie,
                musical, sports, game,
                itTech, kpop, alcohol,
                animalPlant, etc,
                oper); // 운영 상태

        return PopupGuestSearchingDto.fromEntityList(popups);
    } // 비로그인 팝업 검색

    public List<PopupGuestSearchingDto> readGuestBaseList(String text, int page, int size){
        // 검색어 토큰화 및 Full Text 와일드 카드 적용
        String searchText = null;
        if (text != null && text.trim() != ""){
            searchText = prepardSearchUtil.prepareSearchText(text);
        }

        List<Popup> popups = popupRepository.findByTextInNameOrIntroduceBase(searchText, PageRequest.of(page, size));
        return PopupGuestSearchingDto.fromEntityList(popups);
    } // 비로그인 베이스 팝업 검색

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

        fcmService.fcmAddTopic(pushRequestDto.token(), popup, EPopupTopic.REOPEN.getTopicType());


        return "재오픈 수요 체크 되었습니다.";
    }


}
