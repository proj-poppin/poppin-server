package com.poppin.poppinserver.dto.review.response;

import com.poppin.poppinserver.dto.visitorData.response.VisitorDataRvDto;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ReviewUnverDto(
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
        VisitorDataRvDto visitorDataRvDto,

        @NotNull
        String text, /*글*/

        @NotNull
        List<String> imageUrl /*사진 리스트*/ /*후기 테이블도 리스트화 해야 합니다.*/

) {

    public static ReviewUnverDto fromEntity(
            String introduce,
            String posterUrl,
            Boolean isCertificated,
            String nickname,
            LocalDateTime createDate,
            VisitorDataRvDto visitorDataRvDto,
            String text,
            List<String> imageUrl
    ){
        return ReviewUnverDto.builder()
                .introduce(introduce)
                .posterUrl(posterUrl)
                .isCertificated(isCertificated)
                .nickname(nickname)
                .createDate(createDate)
                .visitorDataRvDto(visitorDataRvDto)
                .text(text)
                .imageUrl(imageUrl)
                .build();
    }
}
