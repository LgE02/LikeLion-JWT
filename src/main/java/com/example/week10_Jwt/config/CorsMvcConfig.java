package com.example.week10_Jwt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry){
        corsRegistry.addMapping("/**")
                .allowedOrigins("http://localhost:3000");
        //모든 API 경로에 대해 CORS 정책을 적용
        //단, http://localhost:3000인 요청에 대해서만 CORS 허용.
        // = localhost:3000에서 실행 중일 때 백엔드(Spring)가 이 요청을 허용
    }

}
