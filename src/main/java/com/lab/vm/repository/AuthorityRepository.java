package com.lab.vm.repository;

import com.lab.vm.model.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * packageName : com.lab.vm.repository.AuthorityRepository
 * fileName : AuthorityRepository
 * author : yelee
 * date : 2022-01-18
 * description : AuthorityRepository 권한 정보
 * ===========================================================
 * DATE                  AUTHOR                  NOTE
 * -----------------------------------------------------------
 * 2022-01-18              yelee             최초 생성
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
