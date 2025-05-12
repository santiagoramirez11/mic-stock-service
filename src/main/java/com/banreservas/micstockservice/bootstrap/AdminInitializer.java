package com.banreservas.micstockservice.bootstrap;

import com.banreservas.micstockservice.config.properties.SecurityProperties;
import com.banreservas.micstockservice.model.User;
import com.banreservas.micstockservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final SecurityProperties securityProperties;

    private final static String ACTUATOR_USERNAME = "-actuator";

    @Override
    public void run(String... args) {
        userRepository.findByUsername(securityProperties.getAdmin().username())
                .switchIfEmpty(createAdminUser())
                .subscribe();

        userRepository.findByUsername(securityProperties.getAdmin().username() + ACTUATOR_USERNAME)
                .switchIfEmpty(createActuatorUser())
                .subscribe();
    }

    private Mono<User> createAdminUser() {
        if (!securityProperties.getRoles().contains("ADMIN")) {
            return Mono.error(new IllegalStateException("The role ADMIN is not configured in allowed roles"));
        }

        User adminUser = User.builder()
                .username(securityProperties.getAdmin().username())
                .password(passwordEncoder.encode(securityProperties.getAdmin().password()))
                .roles(Set.of("ADMIN"))
                .build();

        return userRepository.save(adminUser)
                .doOnSuccess(user -> log.info("Admin user created: [{}] ", user.getUsername()));
    }

    private Mono<User> createActuatorUser() {
        if (!securityProperties.getRoles().contains("ACTUATOR")) {
            return Mono.error(new IllegalStateException("The role ACTUATOR is not configured in allowed roles"));
        }

        User actuatorUser = User.builder()
                .username(securityProperties.getAdmin().username() + ACTUATOR_USERNAME)
                .password(passwordEncoder.encode(securityProperties.getAdmin().password()))
                .roles(Set.of("ACTUATOR"))
                .build();

        return userRepository.save(actuatorUser)
                .doOnSuccess(user -> log.info("Actuator user created: [{}] ", user.getUsername()));
    }
}