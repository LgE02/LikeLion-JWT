package com.example.week10_Jwt.jwt;

import com.example.week10_Jwt.dto.CustomUserDetails;
import com.example.week10_Jwt.entity.UserEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
//요청에 대해서 한번만 동작하는 필터
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    //토큰 검증
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //request에서 Authorization 헤더를 찾음
        String authorization = request.getHeader("Authorization");

        //Authorization 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) {

            System.out.println("token null");
            filterChain.doFilter(request, response); //현재 필터에서 다음 필터로 넘김

            //조건이 해당되면 메소드 종료 (필수)
            return;
        }

        System.out.println("authorication now");

        //Bearer 부분 제거 후 순수 토큰만 획득
        String token = authorization.split(" ")[1];

        //토큰 소멸 시간 검증
        //true면 만료됨
        if(jwtUtil.isExpired(token)){
            System.out.println("token expired");
            //현재 필터에서 다음 필터로 넘김
            filterChain.doFilter(request,response);

            //조건이 해당되면 메소드 종료 (필수)
            return;
        }

        //여기까지 실행 된것은 최종적으로 토큰이 검증된 것.

        //일시적인 세션을 하나 만들어서 SecurityContextHolder(시큐리티 세션)에 일시적으로 유저정보를 저장
        //이후 유저 정보를 DB돌지 않아도 사용 가능

        //토큰에서 username과 role 획득
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);


        //userEntity객체 생성
        UserEntity userEntity = UserEntity.builder()
                .username(username)
                .password("temppassword")//임시적 비번(실질적 사용x)
                .role(role)
                .build();


        //UserDetails에 회원 정보 객체 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

        //스프링 시큐리티  Authentication 객체(인증 토큰) 생성
        //유저 세션 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        //SecurityContextHolder에 인증 정보 저장
        SecurityContextHolder.getContext().setAuthentication(authToken);

        //다음 필터로 요청을 넘김
        filterChain.doFilter(request, response);

    }
}
