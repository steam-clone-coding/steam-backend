package com.clonecoding.steam.repository;

import com.clonecoding.steam.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByUid(String uid);

    Optional<User> findUserByEmail(String email);
}
