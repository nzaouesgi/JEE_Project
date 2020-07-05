package fr.esgi.secureupload.security.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JWTProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private final byte[] secret;

    public JWTProvider(@Value("${security.token.secret}") CharSequence secret) {
        this.secret = secret.toString().getBytes();
    }

    public String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        long tokenValidityInMilliseconds = 1000 * 60 * 60 * 24;
        Date validity = new Date(now + tokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(SignatureAlgorithm.HS512, secret)
                .setExpiration(validity)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseToken(token).getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String authToken) {
        try {
            parseToken(authToken);
            return true;

        } catch (JwtException | IllegalArgumentException e) {
            log.info(String.format("Bad token could not be parsed (%s).", e.getMessage()));
            return false;
        }
    }

    private Jws<Claims> parseToken(String authToken) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(authToken);
    }
}