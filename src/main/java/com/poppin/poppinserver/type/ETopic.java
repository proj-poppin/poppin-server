package com.poppin.poppinserver.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ETopic {


    MAGAM_POPUP("MAGAM_POPUP"),                 // 관심 팝업 마감 임박
    CHANGE_INFO_POPUP("CHANGE_INFO_POPUP"),     // 관심 팝업 정보 변경
    HOT_POPUP("HOT_POPUP"),                     // 인기 팝업
    HOOGI("HOOGI"),                             // 방문한 팝업 후기 요청
    CHOOCHUN_POPUP("CHOOCHUN_POPUP"),           // 도움된 후기 알림
    REOPEN_POPUP("REOPEN_POPUP"),               // 재오픈 수요체크 팝업 오픈
    KEYWORD_POPUP("KEYWORD_POPUP"),             // 키워드알림 팝업 오픈
    OPEN_POPUP("OPEN_POPUP"),                   // 관심 팝업 오픈
    JAEBO_POPUP("JAEBO_POPUP"),                 // 제보하기
    BANGMUN_POPUP("BANGMUN_POPUP");             // 방문하기


    private final String topicName;
}
