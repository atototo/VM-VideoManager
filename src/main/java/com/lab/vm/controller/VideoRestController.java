package com.lab.vm.controller;

import com.lab.vm.common.exception.TokenValidationFailedException;
import com.lab.vm.common.exception.UserReqFailedException;
import com.lab.vm.common.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * packageName : com.lab.vm.controller
 * fileName : VideoRestController
 * author : yelee
 * date : 2022-01-18
 * description :
 * ===========================================================
 * DATE                  AUTHOR                  NOTE
 * -----------------------------------------------------------
 * 2022-01-18              yelee             최초 생성
 */
@RequiredArgsConstructor
@Controller
@Slf4j
public class VideoRestController {

    @Value("${file.dir}")
    private String fileDir;
    private final TokenProvider tokenProvider;

    /**
     * methodName : stream
     * author : yelee
     * description : 파일 스트리밍
     * @param req req
     * @param fileName  fileName
     * @param token token
     * @return streaming response body
     * @throws IOException the io exception
     */
    @GetMapping("/video-stream/{fileName}/token/{token}")
    public StreamingResponseBody stream(HttpServletRequest req,@PathVariable String fileName, @PathVariable String token) throws IOException {
        log.info("[ 비디오 스트리밍 진행 ]");
        log.info("[ 비디오 스트리밍 진행 - 토큰 검증 ]");
        // token 유효성 확인
        if (!tokenProvider.validateToken(token)) {
            throw new TokenValidationFailedException("사용자 인증정보가 만료되었습니다. 인증정보 갱신 또는 재로그인 해야합니다");
        }

        var isUser = tokenProvider.getAuthentication(token).getAuthorities()
                .stream()
                .map(Object::toString)
                .anyMatch(t -> t.equals("ROLE_USER"));

        if(!isUser) {
            throw new UserReqFailedException("비디오 재생 권한이 없습니다.");
        }

        log.info("[ 비디오 스트리밍 진행 - 권한 확인 완료 및 스트리밍 진행]");
        String originFileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);
        File file = new File(fileDir + originFileName);
        final InputStream is = new FileInputStream(file);
        return os -> readAndWrite(is, os);
    }

    private void readAndWrite(final InputStream is, OutputStream os) throws IOException {
        byte[] data = new byte[2048];
        int read = 0;
        while ((read = is.read(data)) > 0) {
            os.write(data, 0, read);
        }
        os.flush();
    }
}
