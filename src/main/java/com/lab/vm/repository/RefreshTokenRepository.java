package com.lab.vm.repository;

import com.lab.vm.model.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * packageName : com.lab.vm.repository
 * fileName : RefreshTokenRepository
 * author : isbn8
 * date : 2022-01-19
 * description :
 * ===========================================================
 * DATE                  AUTHOR                  NOTE
 * -----------------------------------------------------------
 * 2022-01-19              isbn8             최초 생성
 */

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByKey(String key);
}
