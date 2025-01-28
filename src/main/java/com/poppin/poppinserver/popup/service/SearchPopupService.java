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
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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

    public PagingResponseDto<List<PopupStoreDto>> readSearchingList(String text, String filteringThreeCategories, String filteringFourteenCategories,
                                               EOperationStatus oper, EPopupSort order, int page, int size,
                                               HttpServletRequest request) {
        Long userId = headerUtil.parseUserId(request);

        List<String> taste = Arrays.stream(filteringThreeCategories.split(",")).toList();
        List<String> prepered = Arrays.stream(filteringFourteenCategories.split(",")).toList();

        // 만약 전부 null(초기화상태)라면, 카테고리 전부 1로 바꿔서 검색어만 검열
        if (Objects.equals(taste.get(0), "")) {
            taste = List.of("market", "display", "experience");
        }

        if (Objects.equals(prepered.get(0), "")) {
            prepered = List.of("fashionBeauty", "characters", "foodBeverage", "webtoonAni", "interiorThings", "movie", "musical", "sports", "game", "itTech", "kpop", "alcohol", "animalPlant", "etc");
        }

        // 카테고리 입력값 유효성 검사
        validateInput(filteringThreeCategories, filteringFourteenCategories);

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
        if (text != null) {
            text = text.trim();
            if (!text.isEmpty()) {
                searchText = prepardSearchUtil.prepareSearchText(text);
            } else {
                text = null;
            }
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

            Page<Popup> popups = popupRepository.findByTextInNameOrIntroduceByBlackList(text, searchText,
                    PageRequest.of(page, size, sort),
                    market, display, experience, // 팝업 형태 3개
                    fashionBeauty, characters, foodBeverage, // 팝업 취향 14개
                    webtoonAni, interiorThings, movie,
                    musical, sports, game,
                    itTech, kpop, alcohol,
                    animalPlant, etc,
                    oper.getStatus(), userId); // 운영 상태

            popupStoreDtos = popupService.getPopupStoreDtos(popups, userId);
            pageInfoDto = PageInfoDto.fromPageInfo(popups);
        } else {
            Page<Popup> popups = popupRepository.findByTextInNameOrIntroduce(text, searchText, PageRequest.of(page, size, sort),
                    market, display, experience, // 팝업 형태 3개
                    fashionBeauty, characters, foodBeverage, // 팝업 취향 14개
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

    private void validateInput(String filteringThreeCategories, String filteringFourteenCategories) {
        // 허용된 카테고리 리스트
        List<String> validThreeCategories = List.of("market", "display", "experience");
        List<String> validFourteenCategories = List.of("fashionBeauty", "characters", "foodBeverage", "webtoonAni",
                "interiorThings", "movie", "musical", "sports", "game", "itTech", "kpop", "alcohol", "animalPlant", "etc");

        // filteringThreeCategories 유효성 검사
        if (filteringThreeCategories != null && !filteringThreeCategories.isEmpty()) {
            List<String> threeCategories = Arrays.stream(filteringThreeCategories.split(","))
                    .filter(category -> !category.isBlank()) // 빈 문자열 무시
                    .toList();

            for (String category : threeCategories) {
                if (!validThreeCategories.contains(category)) {
                    throw new CommonException(ErrorCode.INVALID_THREE_CATEGORY);
                }
            }
        }

        // filteringFourteenCategories 유효성 검사
        if (filteringFourteenCategories != null && !filteringFourteenCategories.isEmpty()) {
            List<String> fourteenCategories = Arrays.stream(filteringFourteenCategories.split(","))
                    .filter(category -> !category.isBlank()) // 빈 문자열 무시
                    .toList();

            for (String category : fourteenCategories) {
                if (!validFourteenCategories.contains(category)) {
                    throw new CommonException(ErrorCode.INVALID_FOURTEEN_CATEGORY);
                }
            }
        }
    }
}
