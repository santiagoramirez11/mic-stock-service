package com.banreservas.micstockservice.service;

import com.banreservas.micstockservice.model.User;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<User> createUser(User user);
}
