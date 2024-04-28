package com.poppin.poppinserver.alarm;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Inform {

    UPDATE("업데이트 알림", "공지사항"),
    EVENT("이벤트 공지사항", "공지사항"),
    ERROR("오류 공지사항", "공지사항");

    private final String title;
    private final String body;
}
