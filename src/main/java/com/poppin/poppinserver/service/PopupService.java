package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.*;
import com.poppin.poppinserver.dto.popup.request.CreatePopupDto;
import com.poppin.poppinserver.dto.popup.request.CreatePreferedDto;
import com.poppin.poppinserver.dto.popup.request.CreateTasteDto;
import com.poppin.poppinserver.dto.popup.request.CreateWhoWithDto;
import com.poppin.poppinserver.dto.popup.response.*;
import com.poppin.poppinserver.dto.review.response.ReviewInfoDto;
import com.poppin.poppinserver.dto.visitorData.response.VisitorDataInfoDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.*;
import com.poppin.poppinserver.specification.PopupSpecification;
import com.poppin.poppinserver.util.SelectRandomUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
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

    private final SelectRandomUtil selectRandomUtil;

    public PopupDto createPopup(CreatePopupDto createPopupDto, List<MultipartFile> images) {
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
                .whoWithPopup(whoWithPopup)
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

        return PopupDto.fromEntity(popup);
    }

    public PopupDetailDto readDetail(Long popupId){
        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        popup.addViewCnt(); // 조회수 + 1

        List<Review> reviews = reviewRepository.findAllByPopupIdOrderByRecommendCntDesc(popupId, PageRequest.of(0,3)); // 후기 추천수 상위 3개

        List<ReviewInfoDto> reviewInfoList = ReviewInfoDto.fromEntityList(reviews, 0);

        VisitorDataInfoDto visitorDataDto = visitorDataService.getVisitorData(popupId); // 방문자 데이터

        Optional<Integer> visitors = realTimeVisitService.showRealTimeVisitors(popupId); // 실시간 방문자

        popupRepository.save(popup);

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



    public String reopenDemand(Long popupId){

        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        popup.addreopenDemandCnt(); // 재오픈 수요 + 1
        popupRepository.save(popup);
        return "재오픈 수요 체크 되었습니다.";
    }
}
