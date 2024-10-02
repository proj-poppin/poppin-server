package com.poppin.poppinserver.alarm.dto.informAlarm.response;

import com.poppin.poppinserver.alarm.domain.InformAlarm;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Builder
public record NoticeDto(
        Long id,
        String title,
        Optional<String> content,
        String createdAt,
        Optional<List<String>> imageUrls,
        Optional<String> contentUrl
) {
    public static NoticeDto fromEntity(InformAlarm informAlarm) {
        List<String> imageUrls = new ArrayList<>();
        imageUrls.add(informAlarm.getInformAlarmImage().getPosterUrl());

        return NoticeDto.builder()
                .id(informAlarm.getId())
                .title(informAlarm.getTitle())
                .content(Optional.ofNullable(informAlarm.getBody()))
                .createdAt(informAlarm.getCreatedAt().toString())
                .imageUrls(Optional.of(imageUrls))
                .build();
    }

    public static List<NoticeDto> fromEntities(List<InformAlarm> informAlarms) {
        List<NoticeDto> noticeDtos = new ArrayList<>();
        for (InformAlarm informAlarm : informAlarms) {
            noticeDtos.add(NoticeDto.fromEntity(informAlarm));
        }
        return noticeDtos;
    }
}
