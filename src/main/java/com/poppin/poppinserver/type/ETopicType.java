package com.poppin.poppinserver.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ETopicType {

    IP("Interest_Popup"),       //관심팝업
    RO("Reopen_Popup");         //재오픈


    private final String type;
}
