package com.sigma.cinematicketpro.repository;

import com.sigma.cinematicketpro.entity.CtpUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<CtpUser, Long> {

  Optional<CtpUser> findByUsername(String username);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);
}
