package com.poppin.poppinserver.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/*

* Class     : FCM 주제 관리
* Author    : sakang
* Date      : 2024-04-30
*
* */


@RequiredArgsConstructor
@Getter
public enum EPopupTopic {

    /* Popup */
    MAGAM("MAGAM_POPUP"),                 // 관심 팝업 마감 임박
    CHANGE_INFO("CHANGE_INFO_POPUP"),     // 관심 팝업 정보 변경
    HOT("HOT_POPUP"),                     // 인기 팝업
    HOOGI("HOOGI"),                             // 방문한 팝업 후기 요청
    CHOOCHUN("CHOOCHUN_POPUP"),           // 도움된 후기 알림
    REOPEN("REOPEN_POPUP"),               // 재오픈 수요체크 팝업 오픈
    KEYWORD("KEYWORD_POPUP"),             // 키워드알림 팝업 오픈
    OPEN("OPEN_POPUP"),                   // 관심 팝업 오픈
    JAEBO("JAEBO_POPUP"),                 // 제보하기
    BANGMUN("BANGMUN_POPUP");             // 방문하기

    private final String topicName;
}
