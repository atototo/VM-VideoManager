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
import java.io.IOException;
import java.util.Optional;


/**
 * packageName : com.lab.vm.controller
 * fileName : FileRestController
 * author : yelee
 * date : 2022-01-18
 * description : 파일 업로드 관련 컨트롤러
 * ===========================================================
 * DATE                  AUTHOR                  NOTE
 * -----------------------------------------------------------
 * 2022-01-18              yelee             최초 생성
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class FileRestController {

    private final FileUploadService fileUploadService;

    private final UserRepository userRepository;
    @Value("${file.dir}")
    private String fileDir;

    /**
     * methodName : uploadFiles
     * author : yelee
     * description : 파일 업로드 기능
     * @param file file
     * @return response entity
     * @throws IOException the io exception
     */
    @PostMapping("/file-upload")
    public ResponseEntity<ApiResponseMessage> uploadFiles(@RequestParam MultipartFile file) throws IOException {
        log.info("[ 파일 업로드 진행 ]");
        // 사용자명 가져와서 DB 사용자 정보 가져온다.
        Optional<User> user = SecurityUtils.getCurrentUsername()
                .flatMap(userRepository::findOneWithAuthoritiesByUsername);

        log.info("[ 파일 업로드 - 사용자 정보 확인 ] :: {}", user.toString());

        //사용자 정보 있을경우 파일 업로드 진행
        if (fileUploadService.uploadFile(file, user.orElseThrow())) {

            return ResponseEntity.ok(new ApiResponseMessage(HttpStatus.OK.value(), "파일 업로드 성공"));
        }

        // 사용자 정보 없는경우 실패 리텅
        return ResponseEntity.ok(new ApiResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), "파일 업로드 실패"));
    }


}
