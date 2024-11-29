package com.auth.auth.service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.access-token.expiration}")
    private long accessTokenExpiration;

    @Value("${security.jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    private Key getSignedKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        return buildToken(claims, email, accessTokenExpiration);
    }

    public String generateRefreshToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        return buildToken(claims, email, refreshTokenExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, String emailAddress, long expiration) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(emailAddress)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignedKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, String emailAddress){
        final String username = extractEmailAddress(token);
        return (username.equals(emailAddress)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
    }

    private Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignedKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractEmailAddress(String token) {
        return extractClaim(token,Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

}
