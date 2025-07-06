package com.igorsouza.games.repositories;

import com.igorsouza.games.models.UserGameSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserGameSearchRepository extends JpaRepository<UserGameSearch, UUID> {
    List<UserGameSearch> findByUserId(UUID id);
}
