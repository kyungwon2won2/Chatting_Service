package com.example.chatserver.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

//토큰 만들어주는 클래스

/*
-----------인코딩----------------------     ---------->token
JWT

header - 알고리즘, 타입
payload - 중요한 정보(아이디, 이메일, 이름 ...)
signature - 암호화된 비밀키값
---------------------------------------
 */

@Component
public class JwtTokenProvider {

    private final String secretKey;
    private final int expiration;
    private Key SECRET_KEY;

    //.yaml 에 있는 값들을 받아옴.
    public JwtTokenProvider(@Value("${jwt.secretKey}") String secretKey, @Value("${jwt.expiration}") int expiration) {
        this.secretKey = secretKey;
        this.expiration = expiration;
        this.SECRET_KEY = new SecretKeySpec(java.util.Base64.getDecoder().decode(secretKey), SignatureAlgorithm.HS512.getJcaName());
    }

    public String createToken(String email, String role) {
        //claims : payloader, setSubject(email) : claims의 key값
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);
        Date now = new Date();
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+expiration*60*1000L))
                .signWith(SECRET_KEY)
                .compact();
        return token;
    }
}
