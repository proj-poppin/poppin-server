package com.poppin.poppinserver.popup.dto.popup.response;

import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.domain.PosterImage;
import com.poppin.poppinserver.review.dto.response.PopupReviewDto;
import com.poppin.poppinserver.visit.dto.visitorData.response.VisitorDataInfoDto;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Builder
public record PopupStoreDto(
        String id,
        String homepageLink,
        boolean isInstagram,
        String name,
        String introduce,
        String address,
        String addressDetail,
        String entranceFee,
        String availableAge,
        boolean parkingAvailable,
        boolean isReservationRequired,
        int reopenDemandCnt,
        int interestScrapCnt,
        int viewCnt,
        String createdAt,
        String editedAt,
        String openDate,
        String closeDate,
        String openTime,
        String closeTime,
        double latitude,
        double longitude,
        String operationExcept,
        String operationStatus,
        String mainImageUrl,
        List<String> imageUrls,
        Optional<List<PopupReviewDto>> review,
        VisitorDataInfoDto visitorData,
        Optional<Integer> realTimeVisit,
        PreferenceDto preferences,
        Boolean isBlocked
) {
    public static PopupStoreDto fromEntity(Popup popup, VisitorDataInfoDto visitorDataDto, Optional<Integer> visitorCnt, Boolean isBlocked) {
        String popupId = String.valueOf(popup.getId());

        List<String> imageUrls = popup.getPosterImages()
                .stream()
                .map(PosterImage::getPosterUrl)
                .sorted()
                .toList();

        return PopupStoreDto.builder()
                .id(popupId)
                .homepageLink(popup.getHomepageLink())
                .isInstagram(popup.getHomepageLink().contains("instagram"))
                .name(popup.getName())
                .introduce(popup.getIntroduce())
                .address(popup.getAddress())
                .addressDetail(popup.getAddressDetail())
                .entranceFee(popup.getEntranceFee())
                .availableAge(popup.getAvailableAge().toString())
                .parkingAvailable(popup.getParkingAvailable())
                .isReservationRequired(popup.getResvRequired())
                .reopenDemandCnt(popup.getReopenDemandCnt())
                .interestScrapCnt(popup.getInterestCnt())
                .viewCnt(popup.getViewCnt())
                .createdAt(popup.getCreatedAt().toString())
                .editedAt(popup.getEditedAt().toString())
                .openDate(popup.getOpenDate().toString())
                .closeDate(popup.getCloseDate().toString())
                .openTime(popup.getOpenTime().toString())
                .closeTime(popup.getCloseTime().toString())
                .latitude(popup.getLatitude())
                .longitude(popup.getLongitude())
                .operationExcept(popup.getOperationExcept())
                .operationStatus(popup.getOperationStatus())
                .mainImageUrl(popup.getPosterUrl())
                .imageUrls(imageUrls)
                .review(Optional.of(PopupReviewDto.fromEntities(popup.getReviews())))
                .visitorData(visitorDataDto)
                .realTimeVisit(visitorCnt)
                .preferences(PreferenceDto.fromPopup(popup))
                .isBlocked(isBlocked)
                .build();
    }

    public static List<PopupStoreDto> fromEntities(List<Popup> popups, List<VisitorDataInfoDto> visitorDataDto,  List<Optional<Integer>> visitorCnt, List<Boolean> isBlocked) {
        List<PopupStoreDto> popupDtos = new ArrayList<>();

        for (int i = 0; i < popups.size(); i++) {
            popupDtos.add(fromEntity(popups.get(i), visitorDataDto.get(i), visitorCnt.get(i), isBlocked.get(i)));
        }

        return popupDtos;
    }

    public static List<PopupStoreDto> fromEntities(List<Popup> popups, List<VisitorDataInfoDto> visitorDataDto,  List<Optional<Integer>> visitorCnt) {
        List<PopupStoreDto> popupDtos = new ArrayList<>();

        for (int i = 0; i < popups.size(); i++) {
            popupDtos.add(fromEntity(popups.get(i), visitorDataDto.get(i), visitorCnt.get(i), false));
        }

        return popupDtos;
    }
}