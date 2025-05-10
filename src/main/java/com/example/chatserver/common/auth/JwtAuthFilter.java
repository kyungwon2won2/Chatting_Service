package com.example.chatserver.common.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthFilter extends GenericFilter {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpservletrequest = (HttpServletRequest) request;
        HttpServletResponse httpservletresponse = (HttpServletResponse) response;
        String token = httpservletrequest.getHeader("Authorization");

        try{
            if(token != null){

                if(!token.startsWith("Bearer ")){
                    throw new AuthenticationServiceException("Bearer 형식이 아닙니다.");
                }
                String jwtToken = token.substring(7);

                //시크릿키를 다시 넣어서 사용자의 페이로더 헤더 부분과 결합해서 다시 암호화를 시켜보는 작업
                //우리가 발행한 토큰인지 아닌지 검증
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(jwtToken)
                        .getBody();
                //Claims == payload 부분 추춘하는 이유 -> Authentication 객체를 생성해주기 위해

                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_"+claims.get("role")));
                UserDetails userDetails = new User(claims.getSubject(), "", authorities);

                //Authentication 객체 생성
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
                //SecurityContextHolder > Context > Authentication : 계층구조 -> 나중에 꺼낼때도 사용
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            chain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            httpservletresponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpservletresponse.setContentType("application/json");
            httpservletresponse.getWriter().write("invalid token");
        }

    }
}
