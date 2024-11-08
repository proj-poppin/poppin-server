package com.poppin.poppinserver.popup.service;

import com.poppin.poppinserver.core.dto.PageInfoDto;
import com.poppin.poppinserver.core.dto.PagingResponseDto;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.type.EOperationStatus;
import com.poppin.poppinserver.core.type.EPopupSort;
import com.poppin.poppinserver.core.util.HeaderUtil;
import com.poppin.poppinserver.core.util.PrepardSearchUtil;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.dto.popup.response.PopupStoreDto;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchPopupService {
    private final PopupRepository popupRepository;

    private final PrepardSearchUtil prepardSearchUtil;

    private final PopupService popupService;
    private final UserQueryUseCase userQueryUseCase;
    private final HeaderUtil headerUtil;

    public PagingResponseDto readSearchingList(String text, String filteringThreeCategories, String filteringFourteenCategories,
                                               EOperationStatus oper, EPopupSort order, int page, int size,
                                               HttpServletRequest request) {
        Long userId = headerUtil.parseUserId(request);

        List<String> taste = Arrays.stream(filteringThreeCategories.split(",")).toList();
        List<String> prepered = Arrays.stream(filteringFourteenCategories.split(",")).toList();

        // 만약 전부 null(초기화상태)라면, 카테고리 전부 1로 바꿔서 검색어만 검열
        log.info("taste: " + taste.size());
        if (Objects.equals(taste.get(0), "")) {
            log.info("isEmpty");
            taste = List.of("market", "display", "experience");
        }

        log.info("prepered: " + prepered.size());
        if (Objects.equals(prepered.get(0), "")) {
            log.info("isEmpty");
            prepered = List.of("fashionBeauty", "characters", "foodBeverage", "webtoonAni", "interiorThings", "movie", "musical", "sports", "game", "itTech", "kpop", "alcohol", "animalPlant", "etc");
        }

        // 팝업 형태 3개
        Boolean market = taste.contains("market") ? true : null;
        Boolean display = taste.contains("display") ? true : null;
        Boolean experience = taste.contains("experience") ? true : null;

        // 팝업 취향 14개
        Boolean fashionBeauty = prepered.contains("fashionBeauty") ? true : null;
        Boolean characters = prepered.contains("characters") ? true : null;
        Boolean foodBeverage = prepered.contains("foodBeverage") ? true : null;
        Boolean webtoonAni = prepered.contains("webtoonAni") ? true : null;
        Boolean interiorThings = prepered.contains("interiorThings") ? true : null;
        Boolean movie = prepered.contains("movie") ? true : null;
        Boolean musical = prepered.contains("musical") ? true : null;
        Boolean sports = prepered.contains("sports") ? true : null;
        Boolean game = prepered.contains("game") ? true : null;
        Boolean itTech = prepered.contains("itTech") ? true : null;
        Boolean kpop = prepered.contains("kpop") ? true : null;
        Boolean alcohol = prepered.contains("alcohol") ? true : null;
        Boolean animalPlant = prepered.contains("animalPlant") ? true : null;
        Boolean etc = prepered.contains("etc") ? true : null;

        // 검색어 토큰화 및 Full Text 와일드 카드 적용
        String searchText = null;
        if (text != null && text.trim() != "") {
            searchText = prepardSearchUtil.prepareSearchText(text);
        }

        // order에 따른 정렬 방식 설정
        Sort sort = Sort.by("id"); // 기본 정렬은 id에 대한 정렬을 설정
        if (order != null) {
            sort = switch (order) {
                case RECENTLY_OPENED -> Sort.by(Sort.Direction.DESC, "open_date");
                case CLOSING_SOON -> Sort.by(Sort.Direction.ASC, "close_date");
                case MOST_VIEWED -> Sort.by(Sort.Direction.DESC, "view_cnt");
                case RECENTLY_UPLOADED -> Sort.by(Sort.Direction.DESC, "created_at");
                default -> sort;
            };
        }

        List<PopupStoreDto> popupStoreDtos = null;
        PageInfoDto pageInfoDto = null;
        if (userId != null) {
            User user = userQueryUseCase.findUserById(userId);

            Page<Popup> popups = popupRepository.findByTextInNameOrIntroduceByBlackList(searchText,
                    PageRequest.of(page, size, sort),
                    market, display, experience, // 팝업 형태 3개
                    fashionBeauty, characters, foodBeverage, // 팝업 취향 13개
                    webtoonAni, interiorThings, movie,
                    musical, sports, game,
                    itTech, kpop, alcohol,
                    animalPlant, etc,
                    oper.getStatus(), userId); // 운영 상태

            popupStoreDtos = popupService.getPopupStoreDtos(popups, userId);
            pageInfoDto = PageInfoDto.fromPageInfo(popups);
        } else {
            Page<Popup> popups = popupRepository.findByTextInNameOrIntroduce(searchText, PageRequest.of(page, size, sort),
                    market, display, experience, // 팝업 형태 3개
                    fashionBeauty, characters, foodBeverage, // 팝업 취향 13개
                    webtoonAni, interiorThings, movie,
                    musical, sports, game,
                    itTech, kpop, alcohol,
                    animalPlant, etc,
                    oper.getStatus()); // 운영 상태

            popupStoreDtos = popupService.guestGetPopupStoreDtos(popups);
            pageInfoDto = PageInfoDto.fromPageInfo(popups);
        }


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
                case RECENTLY_OPENED -> Sort.by(Sort.Direction.DESC, "open_date");
                case CLOSING_SOON -> Sort.by(Sort.Direction.ASC, "close_date");
                case MOST_VIEWED -> Sort.by(Sort.Direction.DESC, "view_cnt");
                case RECENTLY_UPLOADED -> Sort.by(Sort.Direction.DESC, "created_at");
                default -> sort;
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
