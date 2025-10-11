package com.example.postapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 오리진(origin) = 프로토콜 + 호스트 + 포트 중 하나라도 다르면 오리진이 다른 경우입니다.
        // 브라우저는 보안상 다른 오리진의 서버에 요청을 보내는 것에 제한을 둡니다.
        // Preflight 요청은 브라우저가 실제 요청을 보내기 전에 사전 확인을 위해 보내는 HTTP OPTIONS 요청입니다.
        registry.addMapping("/**") // 특정 경로 매핑
            .allowedOrigins("http://localhost:5173")  // 허용할 도메인, 모든 도메인을 혀용하려면  "*" 설정한다.
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true) // 쿠키 혀용 여부
            .exposedHeaders("Authorization") // 클라이언트(브라우저)가 응답 헤더 중 Authorization 헤더를 읽을 수 있도록 허용
            .maxAge(3600);  // Preflight 요청 캐싱 시간 (초 단위)
        
    }

}
