package com.example.week10_Jwt.config;

import com.example.week10_Jwt.jwt.JWTUtil;
import com.example.week10_Jwt.jwt.LoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTUtil jwtUtil;

    /**AuthenticationManager는 스프링 시큐리티가 내부적으로 자동 생성해 관리하지만
     커스텀 필터를 추가하는 과정에서 Manager가 필요하여 수동으로 명확히 빈을 등록한다.

    AuthenticationConfiguration : 스프링 시큐리티의 인증 관련 설정을 담고 있는 클래스,
                                  인터페이스인 AuthenticationManager 객체를 만들고 관리한다.
    **/
    //AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;

    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws  Exception{
        return configuration.getAuthenticationManager();
    }

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

        //Before : 해당 필터 전에
        //After : 해당 필터 이후에
        //At : 해당 필터 위치 대체
        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration),jwtUtil), UsernamePasswordAuthenticationFilter.class);

        //세션을 stateless 상태로 설정. 중요!!
        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

}
