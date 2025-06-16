package com.dante.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

//세션 없는 완전한 JWT 기반 인증/인가
//RSA 공개/개인키 기반으로 안전한 토큰 서명/검증
//인증은 클라이언트가 들고 있는 accessToken으로 처리
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${jwt.public.key}")
    RSAPublicKey publicKey;

    @Value("${jwt.private.key}")
    RSAPrivateKey privateKey;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.cors().and()    //REST API에서 브라우저 CORS 요청 허용.
                .csrf().disable()           //JWT 방식은 세션이 없으므로 CSRF 토큰도 불필요 → 비활성화.
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/subreddit")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/posts/")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/posts/**")
                        .permitAll()
                        .requestMatchers("/v2/api-docs",
                                "/configuration/ui",
                                "/swagger-resources/**",
                                "/configuration/security",
                                "/swagger-ui.html",
                                "/webjars/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)//Spring Security에 **"이 애플리케이션은 JWT를 사용하는 OAuth2 Resource Server"**임을 알림. 아래에서 등록한 JwtDecoder를 기반으로 토큰을 자동 검증함.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint()) //인증되지 않은 요청이 들어오면 401 Unauthorized 반환
                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler())  //권한이 없는 요청은 403 Forbidden 반환
                ).build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    JwtDecoder jwtDecoder() {   //클라이언트가 보낸 JWT를 디코딩 및 검증 (public key 사용)
        return NimbusJwtDecoder.withPublicKey(this.publicKey).build();
    }

    //서버에서 JWT를 서명해서 발급할 때 사용
    //RSAKey를 통해 공개/개인키 쌍을 생성하고, JWK Set으로 감쌈
    //NimbusJwtEncoder: Spring Security에서 제공하는 JWT 인코딩 구현체
    @Bean
    JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(this.publicKey).privateKey(this.privateKey).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }
}
