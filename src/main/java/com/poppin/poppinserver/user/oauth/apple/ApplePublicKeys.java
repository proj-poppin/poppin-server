package com.poppin.poppinserver.user.oauth.apple;

import java.util.List;

public record ApplePublicKeys(
        List<ApplePublicKey> keys
) {
}
