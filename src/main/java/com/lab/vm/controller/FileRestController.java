package com.lab.vm.controller;

import com.lab.vm.common.security.SecurityUtils;
import com.lab.vm.model.domain.User;
import com.lab.vm.model.vo.ApiResponseMessage;
import com.lab.vm.repository.UserRepository;
import com.lab.vm.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class FileRestController {

    private final FileUploadService fileUploadService;

    private final UserRepository userRepository;
    @Value("${file.dir}")
    private String fileDir;

    @PostMapping("/file-upload")
    public ResponseEntity<ApiResponseMessage> uploadFiles(@RequestParam MultipartFile file,  HttpServletRequest request) throws IOException {

        log.info("file tostring {}", file.toString());
        log.info("request={}", request);

        log.info("multipartFile={}", file);


        Optional<User> user = SecurityUtils.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByUsername);
        log.info("[사용자 정보 확인 ] :: {}", user.toString());
        log.info("[사용자 권한 정보 확인 ] :: {}", user.get().getAuthorities().toString());


        if (fileUploadService.uploadFile(file, user.get())) {

            return ResponseEntity.ok(new ApiResponseMessage(HttpStatus.OK.value(), "파일 업로드 성공"));
        }

        return ResponseEntity.ok(new ApiResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), "파일 업로드 실패"));
    }


}
