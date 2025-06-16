package com.dante.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.spi.CharsetProvider;

//스프링 부트 자동 설정을 유지하면서 추가로 설정하고 싶은 경우에는 @Configuration + WebMvcConfigurer
//스프링 부트 자동 설정 대신 직접 모든 것을 설정하고 싶은 경우에는 @Configuration + @EnableWebMvc + WebMvcConfigurer
//@EnableWebMvc: Spring의 기본 Web MVC 설정을 사용하지 않고, 개발자가 정의한 설정을 명시적으로 적용하겠다는 의미입니다. (기본 자동 설정을 꺼버리는 효과도 있음)
@EnableWebMvc
@Configuration  //해당 클래스가 Spring 설정 클래스임을 명시합니다.
public class WebConfig implements WebMvcConfigurer {    //WebMvcConfigurer Spring MVC 설정을 커스터마이징할 수 있는 인터페이스입니다.

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
                .allowedOriginPatterns("*") //.allowedOriginPatterns("https://yourdomain.com")
                .allowedMethods("*")
                .maxAge(3600L)
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")  //Swagger UI HTML 파일이 위치한 경로를 명시.
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");    // 자바스크립트/스타일 등 정적 리소스를 제공하는 Webjars 경로 설정.
    }

    //Spring Boot 3 이상에서 Swagger(OpenAPI)를 사용하려면 위 경로 설정이 제대로 돼 있어야 Swagger UI에 접속할 수 있습니다.
}
