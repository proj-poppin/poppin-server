package com.poppin.poppinserver.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EAvailableAge {
    G_RATED("G_RATED"),
    PG_7("PG_7"),
    PG_12("PG_12"),
    PG_15("PG_15"),
    PG_18("PG_18");

    private final String AvailableAgeProvider;
}
