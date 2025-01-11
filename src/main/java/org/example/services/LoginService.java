package org.example.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.repositories.LoginRepository;

public class LoginService {

    private static final Logger logger = LogManager.getLogger(LoginService.class);
    private final LoginRepository loginRepository = new LoginRepository();

    public String login(String username, String password) {
        logger.info("Processing login request for user: {}", username);

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Username or password cannot be null or empty");
        }

        String result = loginRepository.loginUser(username, password);

        logger.info("Login result for user {}: {}", username, result);

        return result;
    }
}
