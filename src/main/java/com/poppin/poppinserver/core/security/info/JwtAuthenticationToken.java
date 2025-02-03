package com.poppin.poppinserver.core.security.info;

import com.poppin.poppinserver.user.domain.type.EUserRole;
import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private final Long userId;
    private final EUserRole role;

    public JwtAuthenticationToken(Collection<? extends GrantedAuthority> authorities, Long id, EUserRole role) {
        super(authorities);
        this.userId = id;
        this.role = role;
    }

    @Override
    public Object getCredentials() {
        return this.role;
    }

    @Override
    public Object getPrincipal() {
        return this.userId;
    }
}
