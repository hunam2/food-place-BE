package com.github.foodplacebe.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenConfig {
    private final UserDetailsService userDetailsService;

    @Value("${jwtpassword.source}")
    private String keySource;
    private String key;
    @PostConstruct
    public void setUp(){
        key = Base64.getEncoder()
                .encodeToString(keySource.getBytes());
    }

    private long tokenExpiry = 1000L * 60 *60;

    public boolean validateToken(String token){
        try{//.ExpiredJwtException 토큰검증실패시 발생 따로 커스텀익셉션 설정안함.
            Claims claims = Jwts.parser()
                    .setSigningKey(key).parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().after(new Date());
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public Authentication getAuthentication(String jwtToken) {
        String email = Jwts.parser().setSigningKey(key).parseClaimsJws(jwtToken).getBody().getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String createToken(String email, List<String> roles) {
        Date now = new Date();
        String token = Jwts.builder()
                .setIssuedAt(now)
                .setSubject(email)
                .claim("roles",roles)
                .setExpiration(new Date(now.getTime()+tokenExpiry))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
        return token;
    }



}
