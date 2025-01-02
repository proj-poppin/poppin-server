package com.poppin.poppinserver.review.dto.response;

import com.poppin.poppinserver.visit.dto.visitorData.response.VisitorDataRvDto;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/*마이페이지 후기 조회*/
@Builder
public record ReviewDto(

        @NotNull
        String introduce,

        @NotNull
        String posterUrl,

        @NotNull
        Boolean isCertificated,

        @NotNull
        String nickname,

        @NotNull
        String visitedAt, /*visitor class createdAt*/

        @NotNull
        String createdAt, /*review class createdAt*/

        @NotNull
        VisitorDataRvDto visitorData,

        @NotNull
        String text,

        @NotNull
        List<String> images /*사진 리스트*/

) {

    public static ReviewDto fromEntity(
            String introduce,
            String posterUrl,
            Boolean isCertificated,
            String nickname,
            LocalDateTime visitDate,
            LocalDateTime createDate,
            VisitorDataRvDto visitorDataRvDto,
            String text,
            List<String> images
    ) {
        return ReviewDto.builder()
                .introduce(introduce)
                .posterUrl(posterUrl)
                .isCertificated(isCertificated)
                .nickname(nickname)
                .visitedAt(visitDate.toString())
                .createdAt(createDate.toString())
                .visitorData(visitorDataRvDto)
                .text(text)
                .images(images)
                .build();
    }

}
