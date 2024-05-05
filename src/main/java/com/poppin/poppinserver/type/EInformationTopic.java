package com.poppin.poppinserver.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EInformationTopic {

    NOTI("NOTIFICATION", "NT"),                       // 공지사항
    ERROR("ERROR", "ER"),                             // 에러사항
    EVT("EVENT", "EV");                               // 이벤트

    private final String topicName;
    private final String topicType;

}
