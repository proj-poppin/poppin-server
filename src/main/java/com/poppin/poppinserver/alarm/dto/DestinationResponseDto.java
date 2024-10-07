package com.poppin.poppinserver.alarm.dto;

import lombok.Builder;

@Builder
public record DestinationResponseDto(
        String outerLink,
        String screen,
        String inAppLink,
        String inAppLinkDetailUrl,
        String noticeId,
        String popupId,
        String sharerId,
        String signUpEmail,
        String screenAfterSignup
) {
    public static DestinationResponseDto fromProperties(String outerLink, String screen, String inAppLink,
                                                        String inAppLinkDetailUrl, String noticeId, String popupId,
                                                        String sharerId,
                                                        String signUpEmail, String screenAfterSignup) {
        return DestinationResponseDto.builder()
                .outerLink(outerLink)
                .screen(screen)
                .inAppLink(inAppLink)
                .inAppLinkDetailUrl(inAppLinkDetailUrl)
                .noticeId(noticeId)
                .popupId(popupId)
                .sharerId(sharerId)
                .signUpEmail(signUpEmail)
                .screenAfterSignup(screenAfterSignup)
                .build();
    }
}
