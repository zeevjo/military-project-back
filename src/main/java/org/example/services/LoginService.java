package org.example.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.repositories.LoginRepository;
import org.example.utils.Properties;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LoginService {

    private static final Logger logger = LogManager.getLogger(LoginService.class);
    private final LoginRepository loginRepository = new LoginRepository();

    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(Properties.get("SECRET_KEY").getBytes());


    public Map<String, Object> login(String username, String password) {
        logger.info("Processing login request for user: {}", username);

        String result = loginRepository.loginUser(username, password);
        logger.info("Login result for user {}: {}", username, result);

        Map<String, Object> response = new HashMap<>();

        if ("Login successful".equalsIgnoreCase(result)) {
            String token = generateJwtToken(username);
            logger.info("Generated JWT token for user {}: {}", username, token);

            response.put("success", true);
            response.put("message", "Login successful");
            response.put("token", token);
        } else {
            logger.warn("Login failed for user {}", username);

            response.put("success", false);
            response.put("message", "Login failed");
            response.put("token", null);
        }

        return response;
    }

    private String generateJwtToken(String username) {
        long expirationTimeMillis = System.currentTimeMillis() + 9 * 60 * 60 * 1000;
        Date expirationDate = new Date(expirationTimeMillis);
        return Jwts.builder()
                .setSubject(username)
                .setIssuer("org.example")
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(SECRET_KEY)
                .compact();
    }
}