package com.poppin.poppinserver.dto.review.response;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

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
        LocalDateTime createdAt /*후기 생성 일자*/

) {
        public static ReviewFinishDto fromEntity(Long reviewId, Long popupId, String name, Boolean isCertificated, LocalDateTime createdAt){
               return  ReviewFinishDto.builder()
                        .reviewId(reviewId)
                        .popupId(popupId)
                        .name(name)
                        .isCertificated(isCertificated)
                        .createdAt(createdAt)
                        .build();
        }
}
