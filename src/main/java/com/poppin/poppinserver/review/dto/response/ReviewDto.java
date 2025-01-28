package com.poppin.poppinserver.review.dto.response;

import com.poppin.poppinserver.visit.dto.visitorData.response.VisitorDataRvDto;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

/*마이페이지 후기 조회*/
@Builder
public record ReviewDto(

        @NotNull
        String profile,

        @NotNull
        String introduce,

        @NotNull
        String posterUrl,

        @NotNull
        Boolean isCertified,

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
            String profile,
            String introduce,
            String posterUrl,
            Boolean isCertified,
            String nickname,
            String visitDate,
            String createDate,
            VisitorDataRvDto visitorDataRvDto,
            String text,
            List<String> images
    ) {
        return ReviewDto.builder()
                .profile(profile)
                .introduce(introduce)
                .posterUrl(posterUrl)
                .isCertified(isCertified)
                .nickname(nickname)
                .visitedAt(visitDate)
                .createdAt(createDate)
                .visitorData(visitorDataRvDto)
                .text(text)
                .images(images)
                .build();
    }

}
