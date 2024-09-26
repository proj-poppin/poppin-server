package com.poppin.poppinserver.popup.dto.popup.response;

import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.domain.PosterImage;
import com.poppin.poppinserver.review.dto.review.response.PopupReviewDto;
import com.poppin.poppinserver.visit.dto.visitorData.response.VisitorDataDto;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Builder
public record PopupStoreDto(
        Long id,
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
        VisitorDataDto visitorData,
        int realTimeVisit,
        PreferenceDto preferences
) {
    public static PopupStoreDto fromEntity(Popup popup) {
        List<String> imageUrls = popup.getPosterImages()
                .stream()
                .map(PosterImage::getPosterUrl)
                .toList();

        return PopupStoreDto.builder()
                .id(popup.getId())
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
                //.visitorData(null)
                // .realTimeVisit()
                .preferences(PreferenceDto.fromPopup(popup))
                .build();
    }
}