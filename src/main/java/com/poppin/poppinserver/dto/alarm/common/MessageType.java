package com.poppin.poppinserver.dto.alarm.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageType {

    REOPEN_POPUP_ALARM("팝업이 재오픈 하였습니다!", "$$$ 팝업이 재오픈 하였습니다! 방문해보세요"),
    INTERESTED_POPUP_UPDATE_ALARM("관심 팝업 정보가 변경되었습니다", " 관심 팝업 등록한 $$$ 팝업 정보가 변경되었습니다! 방문해보세요"),
    INTERESTED_POPUP_OPEN_ALARM("관심 등록한 팝업 오픈 완료", "관심 팝업 등록한 $$$ 팝업이 오픈했습니다! 방문해보세요"),
    TEST_ALARM("테스트 알림", "테스트 알림입니다.")
    ;

    String title;
    String body;

}
