package com.lab.vm.repository;

import com.lab.vm.model.domain.User;
import com.lab.vm.model.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {
}
