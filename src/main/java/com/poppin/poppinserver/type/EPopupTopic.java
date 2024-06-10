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
    MAGAM("MAGAM_POPUP", "MG"),                      // 관심 팝업 마감 임박 -> 관심 누른 팝업이 <마감일 오픈시간> 기준 24시간 전에 알림 (팝업기간이 하루인 경우 제외,기간이 하루 미만으로 남았을 때 관심팝업으로 등록시 제외) (해당 팝업 상세로 이동)
    CHANGE_INFO("CHANGE_INFO_POPUP","CI"),          // 관심 팝업 정보 변경 -> 관심 누른 팝업의 정봑 업데이트 되었을 때 알림 (해당 팝업 상세로 이동)
    HOT("HOT_POPUP", "HT"),                         // 인기 팝업 -> 일주일에 한번씩 알림 (홈으로 이동)
    HOOGI("HOOGI", "HG"),                           // 방문한 팝업 후기 요청 -> ‘방문하기’버튼 누르고 3시간 뒤에 알림 (마이페이지 - 후기작성하기 페이지로 이동)
    CHOOCHUN("CHOOCHUN_POPUP", "CH" ),              // 도움된 후기 알림 -> 유저가 남긴 후기에 좋아요가 달릴 경우 알림 (해당 팝업 후기 페이지로 이동)
    REOPEN("REOPEN_POPUP", "RO"),                   // 재오픈 수요체크 팝업 오픈 -> 재오픈 버튼을 눌러놨던 팝업스토어가 다시 오픈했을 경우 알림 (해당 팝업 상세로 이동)
    KEYWORD("KEYWORD_POPUP", "KW" ),                // 키워드알림 팝업 오픈 -> 키워드 설정에서 설정한 키워드에 해당하는 팝업이 오픈했을 때 알림 (해당 팝업 상세로 이동)
    OPEN("OPEN_POPUP", "OP"),                       // 관심 팝업 오픈 -> 관심 누른 팝업이 오픈했을 때 알림 (해당 팝업 상세로 이동)
    JAEBO("JAEBO_POPUP", "JB" ),                    // 제보하기 ->  14일에 한번씩 알림 (마이페이지-제보하기로 이동)
    BANGMUN("BANGMUN_POPUP", "BM");                 // 방문하기 -> 유저가 팝업스토어 50m이내에 위치했을 때 알림

    private final String topicName;
    private final String topicType;
}
