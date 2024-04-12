package com.poppin.poppinserver.dto.review.response;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ReviewFinishDto(

        @NotNull
        String introduce,

        @NotNull
        String posterUrl,

        @NotNull
        String nickname, /*user nickname*/

        @NotNull
        LocalDateTime visitDate, /*visitor class createdAt*/

        @NotNull
        LocalDateTime createDate, /*review class createdAt*/

        @NotNull
        String text, /*글*/

        @NotNull
        List<String> imageUrl /*사진 리스트*/ /*후기 테이블도 리스트화 해야 합니다.*/

) {

    public static ReviewFinishDto fromEntity(
            String introduce,
            String posterUrl,
            String nickname,
            LocalDateTime visitDate,
            LocalDateTime createDate,
            String text,
            List<String> imageUrl
    ){
        return ReviewFinishDto.builder()
                .introduce(introduce)
                .posterUrl(posterUrl)
                .nickname(nickname)
                .visitDate(visitDate)
                .createDate(createDate)
                .text(text)
                .imageUrl(imageUrl)
                .build();
    }

}
