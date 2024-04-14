package com.poppin.poppinserver.dto.review.response;

import com.poppin.poppinserver.dto.visitorData.response.VisitorDataRvDto;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/*마이페이지 인증후기 조회*/
@Builder
public record ReviewVerDto(

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
        VisitorDataRvDto visitorDataRvDto,

        @NotNull
        String text, /*글*/

        @NotNull
        List<String> imageUrl /*사진 리스트*/ /*후기 테이블도 리스트화 해야 합니다.*/

) {

    public static ReviewVerDto fromEntity(
            String introduce,
            String posterUrl,
            Boolean isCertificated,
            String nickname,
            LocalDateTime visitDate,
            LocalDateTime createDate,
            VisitorDataRvDto visitorDataRvDto,
            String text,
            List<String> imageUrl
    ){
        return ReviewVerDto.builder()
                .introduce(introduce)
                .posterUrl(posterUrl)
                .isCertificated(isCertificated)
                .nickname(nickname)
                .visitDate(visitDate)
                .createDate(createDate)
                .visitorDataRvDto(visitorDataRvDto)
                .text(text)
                .imageUrl(imageUrl)
                .build();
    }

}
