package com.poppin.poppinserver.review.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/*마이페이지 - 작성 완료 후기 생성 dto*/
@Builder
public record ReviewFinishDto(
        @NotNull
        Long reviewId,    /*리뷰 id*/

        @NotNull
        Long popupId,     /*팝업 id*/

        @NotNull
        String name, /*팝업 메인 글*/

        @NotNull
        Boolean isCertificated, /*인증 후기 여부*/

        @NotNull
        LocalDateTime createdAt, /*후기 생성 일자*/

        @NotNull
        List<String> imageList /*팝업 이미지*/

) {
    public static ReviewFinishDto fromEntity(Long reviewId, Long popupId, String name, Boolean isCertificated,
                                             LocalDateTime createdAt, List<String> imageList) {
        return ReviewFinishDto.builder()
                .reviewId(reviewId)
                .popupId(popupId)
                .name(name)
                .isCertificated(isCertificated)
                .createdAt(createdAt)
                .imageList(imageList)
                .build();
    }
}
