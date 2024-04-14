package com.poppin.poppinserver.dto.visitorData.response;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/*인증, 미인증 후기 조회에 보이는 방문자 데이터*/
@Builder
public record VisitorDataRvDto(
        @NotNull
        String visitDate, /*방문일시*/

        @NotNull
        String satisfaction, /*만족도*/

        @NotNull
        String congestion /*혼잡도*/
) {
    public static VisitorDataRvDto fromEntity(
            String visitDate,
            String satisfaction,
            String congestion
    ){
        return VisitorDataRvDto.builder()
                .visitDate(visitDate)
                .satisfaction(satisfaction)
                .congestion(congestion)
                .build();
    }
}
