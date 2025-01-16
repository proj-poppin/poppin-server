package com.poppin.poppinserver.alarm.dto;

import lombok.Builder;

@Builder
public record NotificationResponseDto(
        String id,
        String userId,
        String type,
        String category,
        String title,
        String content,
        String detail,
        String iconUrl,
        Boolean checked,
        String createdAt,
        String popupId,
        String notificationId,
        DestinationResponseDto destination
) {
    public static NotificationResponseDto fromProperties(String id, String userId, String type, String category,
                                                         String title,
                                                         String content, String detail, String iconUrl, Boolean checked,
                                                         String createdAt,
                                                         String popupId, String notificationId,
                                                         DestinationResponseDto destination) {
        return NotificationResponseDto.builder()
                .id(id)
                .userId(userId)
                .type(type)
                .category(category)
                .title(title)
                .content(content)
                .detail(detail)
                .iconUrl(iconUrl)
                .checked(checked)
                .createdAt(createdAt)
                .popupId(popupId)
                .notificationId(notificationId)
                .destination(destination)
                .build();
    }
}
