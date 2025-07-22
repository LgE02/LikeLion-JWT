package com.example.week10_Jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        //비번 암호화 진행
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        //jwt는 세션은 stateless 방식으로 관리하기에 csrf 공격을 방어하지 않아도 괜찮다.
        http.csrf((auth) -> auth.disable());

        //JWT방식으로 로그인할 것이기에 from 로그인 방식과 httpBasic 인증 방식을 비활성화
        http.formLogin((auth) -> auth.disable());
        http.httpBasic((auth) -> auth.disable());

        //특정 경로에 어떤 권한을 가져야하는지 인가작업
        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/login", "/", "/join").permitAll() //모든 권한 허용
                .requestMatchers("/admin").hasRole("ADMIN") //admin 권한만 허용
                .anyRequest().authenticated()); //그 외는 로그인 했을때만 허용

        //세션을 stateless 상태로 설정. 중요!!
        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

}
