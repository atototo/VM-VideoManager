package com.lab.vm.repository;

import com.lab.vm.model.domain.User;
import com.lab.vm.model.dto.RegisterDto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


/**
 * packageName : com.lab.vm.repository
 * fileName : UserRepository
 * author : yelee
 * date : 2022-01-19
 * description : user 정보 repository
 * ===========================================================
 * DATE                  AUTHOR                  NOTE
 * -----------------------------------------------------------
 * 2022-01-19              yelee             최초 생성
 */
public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = "authorities", type = EntityGraph.EntityGraphType.LOAD)
    Optional<User> findOneWithAuthoritiesByUsername(String username);

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByEmailIgnoreCase(String email);

    Optional<User> findAllByUsername(String username);


    @Query(name="find_user_by_name_dto", nativeQuery = true)
    RegisterDto findUserInfoByName(@Param("username") String username);
}
