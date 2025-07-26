package com.example.week10_Jwt.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.Iterator;

@Controller
@ResponseBody
public class MainController {

    @GetMapping("/")
    public String mainP(){

        //현재 인증된 사용자의 정보가 든 Authentication 객체의 principal의 값을 리턴
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        /** UsernamePasswordAuthenticationToken = Authentication 객체 내부의 getName()에서드 구성
         * public String getName() {
         *     if (this.getPrincipal() instanceof UserDetails) {
         *         return ((UserDetails) this.getPrincipal()).getUsername(); //CustomUserDetail의 getUsername() 메서드
         *     }
         *     return (this.getPrincipal() == null) ? "" : this.getPrincipal().toString();
         * }
         */

        //사용자 권한
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();

        return "Main Controller"+ username + role;
    }
}
