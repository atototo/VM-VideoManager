package com.lab.vm.repository;

import com.lab.vm.model.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * packageName : com.lab.vm.repository
 * fileName : VideoRepository
 * author : yelee
 * date : 2022-01-19
 * description : video 정보 repository
 * ===========================================================
 * DATE                  AUTHOR                  NOTE
 * -----------------------------------------------------------
 * 2022-01-19              yelee             최초 생성
 */
public interface VideoRepository extends JpaRepository<Video, Long> {
}
