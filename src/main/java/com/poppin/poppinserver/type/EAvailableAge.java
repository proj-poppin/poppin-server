package com.poppin.poppinserver.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EAvailableAge {
    G_RATED("전체 이용 가능"),
    PG_7("만 7세 이상"),
    PG_12("만 12세 이상"),
    PG_15("만 15세 이상"),
    PG_18("만 18세 이상");

    private final String AvailableAgeProvider;
}
