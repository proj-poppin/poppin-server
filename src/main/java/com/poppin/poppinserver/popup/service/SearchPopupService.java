package com.poppin.poppinserver.popup.service;

import com.poppin.poppinserver.alarm.repository.FCMTokenRepository;
import com.poppin.poppinserver.alarm.service.AlarmService;
import com.poppin.poppinserver.core.dto.PageInfoDto;
import com.poppin.poppinserver.core.dto.PagingResponseDto;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.type.EOperationStatus;
import com.poppin.poppinserver.core.type.EPopupSort;
import com.poppin.poppinserver.core.util.HeaderUtil;
import com.poppin.poppinserver.core.util.PrepardSearchUtil;
import com.poppin.poppinserver.core.util.SelectRandomUtil;
import com.poppin.poppinserver.interest.repository.InterestRepository;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.dto.popup.response.PopupStoreDto;
import com.poppin.poppinserver.popup.repository.BlockedPopupRepository;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.popup.repository.PosterImageRepository;
import com.poppin.poppinserver.popup.repository.ReopenDemandRepository;
import com.poppin.poppinserver.review.repository.ReviewImageRepository;
import com.poppin.poppinserver.review.repository.ReviewRepository;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.repository.BlockedUserRepository;
import com.poppin.poppinserver.user.repository.UserRepository;
import com.poppin.poppinserver.visit.repository.VisitRepository;
import com.poppin.poppinserver.visit.service.VisitService;
import com.poppin.poppinserver.visit.service.VisitorDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchPopupService {
    private final PopupRepository popupRepository;
    private final ReviewRepository reviewRepository;
    private final PosterImageRepository posterImageRepository;
    private final UserRepository userRepository;
    private final InterestRepository interestRepository;
    private final FCMTokenRepository fcmTokenRepository;
    private final ReopenDemandRepository reopenDemandRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final VisitRepository visitRepository;
    private final BlockedUserRepository blockedUserRepository;
    private final BlockedPopupRepository blockedPopupRepository;
    private final VisitorDataService visitorDataService;
    private final VisitService visitService;
    private final SelectRandomUtil selectRandomUtil;
    private final PrepardSearchUtil prepardSearchUtil;

    private final AlarmService alarmService;
    private final HeaderUtil headerUtil;
    private final PopupService popupService;

    public PagingResponseDto readSearchingList(String text, String taste, String prepered,
                                               EOperationStatus oper, EPopupSort order, int page, int size,
                                               Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        // 카테고리 요청 코드 길이 유효성 체크
        if (taste.length() < 3 || prepered.length() < 14) {
            throw new CommonException(ErrorCode.INVALID_CATEGORY_REQUEST);
        }

        // 만약 전부 null(초기화상태)라면, 카테고리 전부 1로 바꿔서 검색어만 검열
        log.info("taste: " + taste);
        if (taste.equals("000")) {
            taste = "111";
        }
        log.info("prepered: " + prepered);
        if (prepered.equals("00000000000000")) {
            prepered = "11111111111111";
        }

        // 팝업 형태 3개
        Boolean market = (taste.charAt(0) == '1') ? true : null;
        Boolean display = (taste.charAt(1) == '1') ? true : null;
        Boolean experience = (taste.charAt(2) == '1') ? true : null;

        // 팝업 취향 14개
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
        if (text != null && text.trim() != "") {
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

        Page<Popup> popups = popupRepository.findByTextInNameOrIntroduceByBlackList(searchText,
                PageRequest.of(page, size, sort),
                market, display, experience, // 팝업 형태 3개
                fashionBeauty, characters, foodBeverage, // 팝업 취향 13개
                webtoonAni, interiorThings, movie,
                musical, sports, game,
                itTech, kpop, alcohol,
                animalPlant, etc,
                oper.getStatus(), userId); // 운영 상태

        List<PopupStoreDto> popupStoreDtos = popupService.getPopupStoreDtos(popups, userId);
        PageInfoDto pageInfoDto = PageInfoDto.fromPageInfo(popups);

        return PagingResponseDto.fromEntityAndPageInfo(popupStoreDtos, pageInfoDto);
    } // 로그인 팝업 검색

    public PagingResponseDto readBaseList(String text, int page, int size, Long userId) {
        // 검색어 토큰화 및 Full Text 와일드 카드 적용
        String searchText = null;
        if (text != null && text.trim() != "") {
            searchText = prepardSearchUtil.prepareSearchText(text);
        }

        Page<Popup> popups = popupRepository.findByTextInNameOrIntroduceBaseByBlackList(searchText,
                PageRequest.of(page, size), userId); // 운영 상태

        List<PopupStoreDto> popupStoreDtos = popupService.getPopupStoreDtos(popups, userId);
        PageInfoDto pageInfoDto = PageInfoDto.fromPageInfo(popups);

        return PagingResponseDto.fromEntityAndPageInfo(popupStoreDtos, pageInfoDto);
    } // 로그인 베이스 팝업 검색

    public PagingResponseDto readGuestSearchingList(String text, String taste, String prepered,
                                                    EOperationStatus oper, EPopupSort order, int page, int size) {
        // 카테고리 요청 코드 길이 유효성 체크
        if (taste.length() < 3 || prepered.length() < 14) {
            throw new CommonException(ErrorCode.INVALID_CATEGORY_REQUEST);
        }

        // 만약 전부 null(초기화상태)라면, 카테고리 전부 1로 바꿔서 검색어만 검열
        log.info("taste: " + taste);
        if (taste.equals("000")) {
            taste = "111";
        }
        log.info("prepered: " + prepered);
        if (prepered.equals("00000000000000")) {
            prepered = "11111111111111";
        }

        // 팝업 형태 3개
        Boolean market = (taste.charAt(0) == '1') ? true : null;
        Boolean display = (taste.charAt(1) == '1') ? true : null;
        Boolean experience = (taste.charAt(2) == '1') ? true : null;

        // 팝업 취향 14개
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
        if (text != null && text.trim() != "") {
            searchText = prepardSearchUtil.prepareSearchText(text);
        }

        // order에 따른 정렬 방식 설정
        Sort sort = Sort.by("id"); // 기본 정렬은 id에 대한 정렬을 설정
        if (order != null) {
            sort = switch (order) {
                case OPEN -> Sort.by(Sort.Direction.DESC, "open_date");
                case CLOSE -> Sort.by(Sort.Direction.ASC, "close_date");
                case VIEW -> Sort.by(Sort.Direction.DESC, "view_cnt");
                case UPLOAD -> Sort.by(Sort.Direction.DESC, "created_at");
            };
        }

        Page<Popup> popups = popupRepository.findByTextInNameOrIntroduce(searchText, PageRequest.of(page, size, sort),
                market, display, experience, // 팝업 형태 3개
                fashionBeauty, characters, foodBeverage, // 팝업 취향 13개
                webtoonAni, interiorThings, movie,
                musical, sports, game,
                itTech, kpop, alcohol,
                animalPlant, etc,
                oper.getStatus()); // 운영 상태

        List<PopupStoreDto> popupStoreDtos = popupService.guestGetPopupStoreDtos(popups);
        PageInfoDto pageInfoDto = PageInfoDto.fromPageInfo(popups);

        return PagingResponseDto.fromEntityAndPageInfo(popupStoreDtos, pageInfoDto);
    } // 비로그인 팝업 검색

    public PagingResponseDto readGuestBaseList(String text, int page, int size) {
        // 검색어 토큰화 및 Full Text 와일드 카드 적용
        String searchText = null;
        if (text != null && text.trim() != "") {
            searchText = prepardSearchUtil.prepareSearchText(text);
        }

        Page<Popup> popups = popupRepository.findByTextInNameOrIntroduceBase(searchText, PageRequest.of(page, size));

        List<PopupStoreDto> popupStoreDtos = popupService.guestGetPopupStoreDtos(popups);
        PageInfoDto pageInfoDto = PageInfoDto.fromPageInfo(popups);

        return PagingResponseDto.fromEntityAndPageInfo(popupStoreDtos, pageInfoDto);
    } // 비로그인 베이스 팝업 검색
}
