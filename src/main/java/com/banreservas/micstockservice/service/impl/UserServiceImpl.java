package com.banreservas.micstockservice.service.impl;

import com.banreservas.micstockservice.config.properties.SecurityProperties;
import com.banreservas.micstockservice.model.User;
import com.banreservas.micstockservice.repository.UserRepository;
import com.banreservas.micstockservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final SecurityProperties securityProperties;

    @Override
    public Mono<User> createUser(User user) {
        for (String role : user.getRoles()) {
            if (!isValidRole(role)) {
                return Mono.error(new IllegalArgumentException("Invalid role: " + role));
            }
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    boolean isValidRole(String role) {
        return securityProperties.getRoles().contains(role);
    }
}
