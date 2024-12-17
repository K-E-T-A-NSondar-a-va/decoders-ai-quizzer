package com.decoder.aiquizzer.service.jwt;

import com.decoder.aiquizzer.entity.UserCredential;
import com.decoder.aiquizzer.exception.InvalidJwtException;
import com.decoder.aiquizzer.repository.UserCredentialRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtUtil {

    @Autowired
    private UserCredentialRepository userCredentialRepository;

    private Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private static final String ENCRYPTION_KEY = "458564494FGD561448984D5849849F654G48R84964D5465484F498498498E49848R84484G8D487487854";

    public String createToken(String username) {
        logger.info("creating token...");
        Map<String, Object> claims = new HashMap<>();
        claims.put("username",username);
        return generateToken(claims);
    }

    private String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (1000L *60*60*24*5)))
                .compact();
    }

    private Key getKey() {
        logger.info("generating key");
        byte[] keyBytes = Decoders.BASE64URL.decode(ENCRYPTION_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validateJwt(String token) throws IOException {

        String username = getJwtUsername(token);
        if (username == null || username.isEmpty()) {
            throw new InvalidJwtException("Username not found in JWT");
        }

        UserCredential credential = userCredentialRepository.findByUsername(username).orElseThrow(() -> new InvalidJwtException("invalid token signature"));

        if (credential == null) {
            throw new InvalidJwtException("User not found");
        }

        Claims claims = extractJwtClaims(token);
        Date expiration = claims.getExpiration();
        if (expiration == null || expiration.before(new Date())) {
            throw new InvalidJwtException("Token expired");
        }

        return true;
    }

    public String getJwtUsername(String token) {
        return extractJwtClaims(token).get("username").toString();
    }

    private Claims extractJwtClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
