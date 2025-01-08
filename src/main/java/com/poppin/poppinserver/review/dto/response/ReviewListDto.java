package com.poppin.poppinserver.review.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/*마이페이지 - 작성 완료 후기 생성 dto*/
@Builder
public record ReviewListDto(
        @NotNull
        String reviewId,    /*리뷰 id*/

        @NotNull
        String popupId,     /*팝업 id*/

        @NotNull
        String name, /*팝업 메인 글*/

        @NotNull
        Boolean isCertified, /*인증 후기 여부*/

        @NotNull
        String createdAt, /*후기 생성 일자*/

        @NotNull
        String imageUrl /*팝업 이미지, 하나만*/

) {
    public static ReviewListDto fromEntity(Long reviewId, Long popupId, String name, Boolean isCertified,
                                           String createdAt, String imageUrl) {
        return ReviewListDto.builder()
                .reviewId(String.valueOf(reviewId))
                .popupId(String.valueOf(popupId))
                .name(name)
                .isCertified(isCertified)
                .createdAt(createdAt)
                .imageUrl(imageUrl)
                .build();
    }
}
