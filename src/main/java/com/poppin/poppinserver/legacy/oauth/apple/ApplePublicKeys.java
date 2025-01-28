package com.poppin.poppinserver.legacy.oauth.apple;

import java.util.List;

public record ApplePublicKeys(
        List<ApplePublicKey> keys
) {
}
