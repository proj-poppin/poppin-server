package com.poppin.poppinserver.review.dto.response;

import com.poppin.poppinserver.visit.dto.visitorData.response.VisitorDataRvDto;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/*마이페이지 인증후기 조회*/
@Builder
public record ReviewCertiDto(

        @NotNull
        String introduce,

        @NotNull
        String posterUrl,

        @NotNull
        Boolean isCertificated,

        @NotNull
        String nickname, /*user nickname*/

        @NotNull
        LocalDateTime visitDate, /*visitor class createdAt*/

        @NotNull
        LocalDateTime createDate, /*review class createdAt*/

        @NotNull
        VisitorDataRvDto visitorData,

        @NotNull
        String text, /*글*/

        @NotNull
        List<String> imageUrl /*사진 리스트*/ /*후기 테이블도 리스트화 해야 합니다.*/

) {

    public static ReviewCertiDto fromEntity(
            String introduce,
            String posterUrl,
            Boolean isCertificated,
            String nickname,
            LocalDateTime visitDate,
            LocalDateTime createDate,
            VisitorDataRvDto visitorDataRvDto,
            String text,
            List<String> imageUrl
    ) {
        return ReviewCertiDto.builder()
                .introduce(introduce)
                .posterUrl(posterUrl)
                .isCertificated(isCertificated)
                .nickname(nickname)
                .visitDate(visitDate)
                .createDate(createDate)
                .visitorData(visitorDataRvDto)
                .text(text)
                .imageUrl(imageUrl)
                .build();
    }

}
