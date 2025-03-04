package com.poppin.poppinserver.popup.service;

import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.util.SelectRandomUtil;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.domain.PreferedPopup;
import com.poppin.poppinserver.popup.domain.TastePopup;
import com.poppin.poppinserver.popup.repository.PopupQueryRepository;
import com.poppin.poppinserver.popup.repository.specification.PopupSpecification;
import com.poppin.poppinserver.popup.usecase.PopupQueryUseCase;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class PopupQueryService implements PopupQueryUseCase {
    private final PopupQueryRepository popupQueryRepository;

    private final UserQueryUseCase userQueryUseCase;

    private final SelectRandomUtil selectRandomUtil;

    @Override
    public Popup findPopupById(Long popupId) {
        return popupQueryRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));
    }

    @Override
    public Popup findPopupByIdElseNull(Long popupId) {
        return popupQueryRepository.findById(popupId)
                .orElse(null);
    }

    @Override
    public List<Popup> findHotPopupList() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime startOfDay = yesterday.atStartOfDay();
        LocalDateTime endOfDay = yesterday.plusDays(1).atStartOfDay();

        return popupQueryRepository.findTopOperatingPopupsByInterestAndViewCount(
                startOfDay,
                endOfDay,
                PageRequest.of(0, 5)
        );
    }

    @Override
    public List<Popup> findHotPopupList(Long userId) {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime startOfDay = yesterday.atStartOfDay();
        LocalDateTime endOfDay = yesterday.plusDays(1).atStartOfDay();

        return popupQueryRepository.findTopOperatingPopupsByInterestAndViewCount(
                startOfDay,
                endOfDay,
                userId,
                PageRequest.of(0, 5)
        );
    }

    @Override
    public List<Popup> findNewPopupList() {
        return popupQueryRepository.findNewOpenPopupByAll(
                PageRequest.of(0, 5)
        );
    }

    @Override
    public List<Popup> findNewPopupList(Long userId) {
        return popupQueryRepository.findNewOpenPopupByAll(
                userId,
                PageRequest.of(0, 5)
        );
    }

    @Override
    public List<Popup> findClosingPopupList() {
        return popupQueryRepository.findClosingPopupByAll(
                PageRequest.of(0, 5)
        );
    }

    @Override
    public List<Popup> findClosingPopupList(Long userId) {
        return popupQueryRepository.findClosingPopupByAll(
                userId,
                PageRequest.of(0, 5)
        );
    }

    @Override
    public List<Popup> findRecommandPopupList(Long userId) {
        // 사용자가 설정한 태그의 팝업들 5개씩 다 가져오기
        // 태그의 개수만큼 랜덤 변수 생성해서 하나 뽑기
        // 5개 선정
        // 관심 테이블에서

        User user = userQueryUseCase.findUserById(userId);

        //취향설정이 되지 않은 유저의 경우
        if (user.getTastePopup() == null || user.getPreferedPopup() == null || user.getWhoWithPopup() == null) {
            return null;
        }

        List<List<Popup>> popups = new ArrayList<>();
        List<String> selectedList = new ArrayList<>();

        // 사용자가 설정한 카테고리에 해당하는 팝업들을 카테고리 별로 5개씩 리스트에 저장
        TastePopup tastePopup = user.getTastePopup();
        List<String> selectedTaste = selectRandomUtil.selectTaste(tastePopup);
        for (String taste : selectedTaste) {
            Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "viewCnt"));
            Specification<Popup> combinedSpec = Specification.where(
                            PopupSpecification.hasTaste(taste, true))
                    .and(PopupSpecification.isOperating())
                    .and(PopupSpecification.isNotBlockedByUser(userId));


            List<Popup> popupList = popupQueryRepository.findAll(combinedSpec, pageable).getContent();

            if (!popupList.isEmpty()) {
                selectedList.add(taste);
                popups.add(popupList);
            }

        }

        PreferedPopup preferedPopup = user.getPreferedPopup();
        List<String> selectedPrefered = selectRandomUtil.selectPreference(preferedPopup);
        for (String prefered : selectedPrefered) {
            Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "viewCnt"));
            Specification<Popup> combinedSpec = Specification.where(
                            PopupSpecification.hasPrefered(prefered, true))
                    .and(PopupSpecification.isOperating())
                    .and(PopupSpecification.isNotBlockedByUser(userId));

            List<Popup> popupList = popupQueryRepository.findAll(combinedSpec, pageable).getContent();

            if (!popupList.isEmpty()) {
                selectedList.add(prefered);
                popups.add(popupList);
            }
        }

        if (selectedList.isEmpty()) {
            return null;
        }
        Random random = new Random();
        Integer randomIndex = random.nextInt(selectedList.size());

        log.info("취향 저격 " + selectedList.get(randomIndex));

        return popups.get(randomIndex);
    }
}
