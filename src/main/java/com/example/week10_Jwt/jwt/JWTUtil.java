package com.example.week10_Jwt.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {
    private final SecretKey secretKey;

    //객체 키 생성
    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

//    //세션 방식
//    @Value("${spring.jwt.secret}")
//    private String secret;
//
//    // @PostConstruct: @Value 주입이 끝난 후 실행됨
//    // JWT 라이브러리(jjwt)는 단순 문자열이 아닌 서명 가능한 Key 객체를 요구함
//    @PostConstruct
//    public void init() {
//        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
//    }

    //토큰 검증 로직
    public String getUsername(String token){
        return Jwts.parser()
                .verifyWith(secretKey) //비밀키 지정
                .build()
                .parseSignedClaims(token) //토큰을 파싱과 함께 서명 검증 진행
                .getPayload().get("username",String.class); //payload에서 username 클레임 값만 문자열로 반환
    }

    public String getRole(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                    //사실 해당 코드로 서명 검증과 payload의 exp를 이용한 만료시간까지 자동으로 검사한다.
              .getPayload().get("role", String.class);
    }

    //토큰이 만료되었는지 검사
    //토큰의 만료시간이(Expiration) 현재 시간보다 이전이지 확인
    //만료 시간을 안봐도 괜찮다면 없어도 괜찮은 로직
    public Boolean isExpired(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload().getExpiration().before(new Date());
    }

    //세션 코드 : 토큰이 완전히 정상적인지, 만료/위변조/포맷 오류 등이 없는지를 한번에 판별
//    // 토큰 유효성 검사 + 만료 검사
//    public boolean validateToken(String token) {
//        try {
//            getClaims(token);
                //아래 메서드를 통해 검증
//            return true;
//        } catch (JwtException | IllegalArgumentException e) {
//            return false;
//        }
//    }

//    // Claim 파싱
//    public Claims getClaims(String token) {
//        return Jwts.parser()
//                .verifyWith((SecretKey) key)
//                .build() // parser 생성
//                .parseSignedClaims(token) // 서명 검증 + 만료 검증 + 파싱!!
//                .getPayload(); // payload 부분(Claims)만 반환
//    }

    //맞으면 성공 토큰 생성 로직
    public String createJwt(String username, String role, Long expiredMs){
        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))//토큰 발행한 현재 시간
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey) //암호화 진행
                .compact();
    }




}
