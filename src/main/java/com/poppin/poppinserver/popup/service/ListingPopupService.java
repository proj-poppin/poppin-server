package com.poppin.poppinserver.popup.service;

import com.poppin.poppinserver.alarm.repository.FCMTokenRepository;
import com.poppin.poppinserver.alarm.service.AlarmService;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.util.HeaderUtil;
import com.poppin.poppinserver.core.util.PrepardSearchUtil;
import com.poppin.poppinserver.core.util.SelectRandomUtil;
import com.poppin.poppinserver.interest.domain.Interest;
import com.poppin.poppinserver.interest.repository.InterestRepository;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.domain.PreferedPopup;
import com.poppin.poppinserver.popup.domain.TastePopup;
import com.poppin.poppinserver.popup.dto.popup.response.InterestedPopupDto;
import com.poppin.poppinserver.popup.dto.popup.response.PopupSummaryDto;
import com.poppin.poppinserver.popup.dto.popup.response.PopupTasteDto;
import com.poppin.poppinserver.popup.repository.BlockedPopupRepository;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.popup.repository.PosterImageRepository;
import com.poppin.poppinserver.popup.repository.ReopenDemandRepository;
import com.poppin.poppinserver.popup.repository.specification.PopupSpecification;
import com.poppin.poppinserver.review.repository.ReviewImageRepository;
import com.poppin.poppinserver.review.repository.ReviewRepository;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.repository.BlockedUserRepository;
import com.poppin.poppinserver.user.repository.UserRepository;
import com.poppin.poppinserver.user.usecase.ReadUserUseCase;
import com.poppin.poppinserver.visit.repository.VisitRepository;
import com.poppin.poppinserver.visit.service.VisitService;
import com.poppin.poppinserver.visit.service.VisitorDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListingPopupService {
    private final PopupRepository popupRepository;

    private final ReadUserUseCase readUserUseCase;
    private final SelectRandomUtil selectRandomUtil;

    public List<PopupSummaryDto> readHotList() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime startOfDay = yesterday.atStartOfDay();
        LocalDateTime endOfDay = yesterday.plusDays(1).atStartOfDay();

        List<Popup> popups = popupRepository.findTopOperatingPopupsByInterestAndViewCount(startOfDay, endOfDay,
                PageRequest.of(0, 5));

        return PopupSummaryDto.fromEntityList(popups);
    } // 인기 팝업 조회

    public List<PopupSummaryDto> readNewList() {

        List<Popup> popups = popupRepository.findNewOpenPopupByAll(PageRequest.of(0, 5));

        return PopupSummaryDto.fromEntityList(popups);
    } // 새로 오픈 팝업 조회

    public List<PopupSummaryDto> readClosingList() {

        List<Popup> popups = popupRepository.findClosingPopupByAll(PageRequest.of(0, 5));

        return PopupSummaryDto.fromEntityList(popups);
    } // 종료 임박 팝업 조회

    @Transactional
    public List<InterestedPopupDto> readInterestedPopups(Long userId) {
        User user = readUserUseCase.findUserById(userId);

        Set<Interest> interest = user.getInterest();

        return InterestedPopupDto.fromEntityList(interest);
    } // 관심 팝업 목록 조회

    @Transactional
    public PopupTasteDto readTasteList(Long userId) {
        // 사용자가 설정한 태그의 팝업들 5개씩 다 가져오기
        // 태그의 개수만큼 랜덤 변수 생성해서 하나 뽑기
        // 5개 선정
        // 관심 테이블에서

        User user = readUserUseCase.findUserById(userId);

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
            Specification<Popup> combinedSpec = Specification.where(PopupSpecification.hasTaste(taste, true))
                    .and(PopupSpecification.isOperating());

            List<Popup> popupList = popupRepository.findAll(combinedSpec, pageable).getContent();

            if (!popupList.isEmpty()) {
                selectedList.add(taste);
                popups.add(popupList);
            }

        }

        PreferedPopup preferedPopup = user.getPreferedPopup();
        List<String> selectedPrefered = selectRandomUtil.selectPreference(preferedPopup);
        for (String prefered : selectedPrefered) {
            Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "viewCnt"));
            Specification<Popup> combinedSpec = Specification.where(PopupSpecification.hasPrefered(prefered, true))
                    .and(PopupSpecification.isOperating());

            List<Popup> popupList = popupRepository.findAll(combinedSpec, pageable).getContent();

            if (!popupList.isEmpty()) {
                selectedList.add(prefered);
                popups.add(popupList);
            }
        }

        Random random = new Random();
        Integer randomIndex = random.nextInt(selectedList.size());

        log.info("취향 저격 " + selectedList.get(randomIndex));

        return new PopupTasteDto(selectedList.get(randomIndex),
                PopupSummaryDto.fromEntityList(popups.get(randomIndex)));
    } // 취향저격 팝업 조회
}
