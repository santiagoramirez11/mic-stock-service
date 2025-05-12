package com.banreservas.micstockservice.service.impl;

import com.banreservas.micstockservice.config.properties.SecurityProperties;
import com.banreservas.micstockservice.security.TokenInfo;
import com.banreservas.micstockservice.service.JwtProviderService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;

import static java.util.stream.Collectors.joining;

@Slf4j
@Service
public class JwtProviderServiceImpl implements JwtProviderService {

    private static final String AUTHORITIES_KEY = "roles";
    private static final String TOKEN_TYPE = "Bearer";
    private final SecretKey hmacKey;
    private final SecurityProperties securityProperties;

    public JwtProviderServiceImpl(SecurityProperties securityProperties) {
        this.hmacKey = Keys.hmacShaKeyFor(securityProperties.getToken().secret().getBytes());
        this.securityProperties = securityProperties;
    }

    @Override
    public TokenInfo generateToken(Authentication authentication) {
        String token = Jwts.builder()
                .subject(authentication.getName())
                .claim(AUTHORITIES_KEY, authentication
                        .getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority).collect(joining(",")))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + securityProperties.getToken().expirationTime()))
                .signWith(hmacKey, Jwts.SIG.HS256)
                .compact();
        return new TokenInfo(token, securityProperties.getToken().expirationTime(), TOKEN_TYPE);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(this.hmacKey)
                    .build().parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.info("Invalid JWT token: [{}]", e.getMessage());
            log.trace("Invalid JWT token: ", e);
        }
        return false;
    }

    @Override
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(hmacKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Object authoritiesClaim = claims.get(AUTHORITIES_KEY);

        Collection<? extends GrantedAuthority> authorities = authoritiesClaim == null
                ? AuthorityUtils.NO_AUTHORITIES
                : AuthorityUtils
                .commaSeparatedStringToAuthorityList(authoritiesClaim.toString());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }
}
