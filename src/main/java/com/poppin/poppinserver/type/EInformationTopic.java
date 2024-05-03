package com.poppin.poppinserver.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EInformationTopic {

    NOTI("NOTIFICATION"),                       // 공지사항
    ERROR("ERROR"),                             // 에러사항
    EVT("EVENT");                               // 이벤트

    private final String topicName;

}
