package com.dante.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtEncoder jwtEncoder;

    @Value("${jwt.expiration.time}")
    private Long jwtExpirationInMillis;

    //Spring Security의 Authentication 객체에서 인증된 사용자 정보를 꺼내 JWT 토큰을 발급합니다.
    //authentication.getPrincipal()은 UserDetails를 구현한 User 객체를 반환합니다.
    //principal.getUsername()을 통해 username을 얻고, 아래의 generateTokenWithUserName()을 호출하여 토큰을 생성합니다.
    public String generateToken(Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        return generateTokenWithUserName(principal.getUsername());
    }

    public String generateTokenWithUserName(String username) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self") //토큰 발급자. 이 시스템에서 직접 발급했음을 나타냄
                .issuedAt(Instant.now()) //발급 시점 (현재 시간)
                .expiresAt(Instant.now().plusMillis(jwtExpirationInMillis)) //	만료 시점 (현재 시간 + 설정된 만료 시간)
                .subject(username)  //토큰의 주체. 일반적으로 사용자 ID 또는 username
                .claim("scope", "ROLE_USER")    //	추가 정보로 "scope" 클레임에 역할(권한) 포함
                .build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue(); //JwtEncoder는 보통 NimbusJwtEncoder가 사용되며, 내부적으로 서명(sign) 을 포함한 JWT를 만들어 반환합니다.
    }

    public Long getJwtExpirationInMillis() {
        return jwtExpirationInMillis;
    }
}
