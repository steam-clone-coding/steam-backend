package com.clonecoding.steam.repository.game;

import com.clonecoding.steam.entity.game.Game;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends CrudRepository<Game, Long> {

    Optional<Game> findByUid(String uid);
}
