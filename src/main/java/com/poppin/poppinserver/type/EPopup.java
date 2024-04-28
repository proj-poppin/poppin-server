package com.poppin.poppinserver.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EPopup {

    JAEBO("나만 알기 아까운 팝업","POPPIN에 제보해주세요"),
    MAGAM("벌써 마감이라니", "마감 D-1"),
    REOPEN("기다리고 기다리던 재오픈", "다시 돌아왔어요~"),
    CHOOCHUN("도움이 되는 후기에요!", "에 좋아요가 달렸어요!"),
    HOTPOPUP("지금 HOT한 팝업은?", "지금 둘러보러가기"),
    KEYWORD("키워드 관련 팝업 등록", "등록되었어요!"),
    OPEN("OPEN!","관심 설정한 팝업이 드디어 오픈했어요~"),
    HOOGI("방문하신 팝업은 어떠셨나요?", "의 후기를 작성해보세요");

    private final String title;
    private final String body;

}
