package com.clonecoding.steam.repository.user;

import com.clonecoding.steam.entity.user.Dummy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DummyRepository extends JpaRepository<Dummy, Long> {
    Optional<Dummy> findByUsername(String username);
}
