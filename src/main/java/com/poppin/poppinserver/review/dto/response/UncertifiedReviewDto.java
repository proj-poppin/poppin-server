package com.poppin.poppinserver.review.dto.response;

import com.poppin.poppinserver.visit.dto.visitorData.response.VisitorDataRvDto;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record UncertifiedReviewDto(
        @NotNull
        String introduce,

        @NotNull
        String posterUrl,

        @NotNull
        Boolean isCertificated,

        @NotNull
        String nickname, /*user nickname*/

        @NotNull
        LocalDateTime createDate, /*review class createdAt*/

        @NotNull
        VisitorDataRvDto visitorData,

        @NotNull
        String text, /*글*/

        @NotNull
        List<String> imageUrl /*사진 리스트*/ /*후기 테이블도 리스트화 해야 합니다.*/

) {

    public static UncertifiedReviewDto fromEntity(
            String introduce,
            String posterUrl,
            Boolean isCertificated,
            String nickname,
            LocalDateTime createDate,
            VisitorDataRvDto visitorDataRvDto,
            String text,
            List<String> imageUrl
    ) {
        return UncertifiedReviewDto.builder()
                .introduce(introduce)
                .posterUrl(posterUrl)
                .isCertificated(isCertificated)
                .nickname(nickname)
                .createDate(createDate)
                .visitorData(visitorDataRvDto)
                .text(text)
                .imageUrl(imageUrl)
                .build();
    }
}
