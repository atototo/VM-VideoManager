package com.lab.vm.repository;

import com.lab.vm.model.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = "authorities", type = EntityGraph.EntityGraphType.LOAD)
    Optional<User> findOneWithAuthoritiesByUsername(String username);

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByEmailIgnoreCase(String email);

    public Optional<User> findAllByUsername(String username);
}
