package com.poppin.poppinserver.security;

import com.poppin.poppinserver.type.EUserRole;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private Long userId;
    // private String email;
    private EUserRole role;

    public JwtAuthenticationToken(Collection<? extends GrantedAuthority> authorities, Long id, EUserRole role) {
        super(authorities);
        // this.email = email;
        this.userId = id;
        this.role = role;
    }

    @Override
    public Object getCredentials() {
        return this.role;
    }

    @Override
    public Object getPrincipal() {
        // return this.email;
        return this.userId;
    }
}
