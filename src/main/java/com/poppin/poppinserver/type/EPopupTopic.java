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
    MAGAM("MAGAM_POPUP", "MG"),                      // 관심 팝업 마감 임박
    CHANGE_INFO("CHANGE_INFO_POPUP","CI"),          // 관심 팝업 정보 변경
    HOT("HOT_POPUP", "HT"),                         // 인기 팝업
HOOGI("HOOGI", "HG"),                               // 방문한 팝업 후기 요청
    CHOOCHUN("CHOOCHUN_POPUP", "CH" ),              // 도움된 후기 알림
    REOPEN("REOPEN_POPUP", "RO"),                   // 재오픈 수요체크 팝업 오픈
    KEYWORD("KEYWORD_POPUP", "KW" ),                // 키워드알림 팝업 오픈
    OPEN("OPEN_POPUP", "OP"),                       // 관심 팝업 오픈
    JAEBO("JAEBO_POPUP", "JB" ),                    // 제보하기
    BANGMUN("BANGMUN_POPUP", "BM");                 // 방문하기

    private final String topicName;
    private final String topicType;
}
