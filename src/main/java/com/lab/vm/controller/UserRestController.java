package com.lab.vm.controller;


import com.lab.vm.common.exception.UserReqFailedException;
import com.lab.vm.common.security.jwt.JWTFilter;
import com.lab.vm.common.security.jwt.JWTToken;
import com.lab.vm.common.security.jwt.TokenProvider;
import com.lab.vm.model.domain.User;
import com.lab.vm.model.dto.LoginDto;
import com.lab.vm.model.dto.RegisterDto;
import com.lab.vm.model.dto.TokenDto;
import com.lab.vm.model.vo.ApiResponseMessage;
import com.lab.vm.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;


/**
 * packageName : com.lab.vm.common.controller
 * fileName : UserRestController
 * author : yelee
 * date : 2022-01-18
 * description : 사용자 정보 관리  REST 컨트롤러
 * ===========================================================
 * DATE                  AUTHOR                  NOTE
 * -----------------------------------------------------------
 * 2022-01-18              yelee             최초 생성
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;



    /**
     * methodName : getActualUser
     * author : Young Lee
     * description : 사용자 정보 조회
     * @return the actual user
     */
    @GetMapping("/user")
    public ResponseEntity<User> getActualUser() {
        log.info("[사용자 정보 조회 진행 ]");
        return ResponseEntity.ok(userService.getUserWithAuthorities().orElseThrow());
    }

    /**
     * methodName : registerUser
     * author : Young Lee
     * description : 사용자 등록
     * @param registerDto dto
     * @return ResponseEntity<ApiResponseMessage> entity
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponseMessage> registerUser(@RequestBody @Valid RegisterDto registerDto) {
        log.info("[ 사용자 등록 진행 ]");
        return ResponseEntity.ok(userService.registerUser(registerDto));
    }

    /**
     * methodName : modifyUser
     * author : Young Lee
     * description : 사용자 정보 수정
     * @param registerDto registerDto
     * @return response entity
     */
    @PutMapping("/modify-user")
    public ResponseEntity<JWTToken> modifyUser(@RequestBody @Valid RegisterDto registerDto){
        log.info("[ 사용자 정보 수정 진행 ]");
        var tokenDto = userService.userModify(registerDto);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + tokenDto.getAccessToken());
        return new ResponseEntity<>(new JWTToken( tokenDto.getAccessToken(), tokenDto.getRefreshToken()), httpHeaders, HttpStatus.OK);
    }


    /**
     * methodName : deleteUser
     * author : Young Lee
     * description : 사용자 탈퇴
     * @param loginDto dto
     * @return response entity
     */
    @DeleteMapping("/delete-user")
    public ResponseEntity<ApiResponseMessage> deleteUser(@RequestBody @Valid LoginDto loginDto){
        log.info("[ 사용자 탈퇴 진행 ]");
        // 탈퇴 처리 : 비활성화
        var user =userService.userDelete(loginDto);
        if(user.isEmpty()) {
            throw new UserReqFailedException("회원 탈퇴 요청 처리 중 문제가 발생하였습니다.");
        }

        return ResponseEntity.ok(new ApiResponseMessage(HttpStatus.OK.value(), "사용자 탈퇴 처리를 완료 하였습니다."));
    }


    /**
     * methodName : reissue
     * author : Young Lee
     * description : 토큰 갱신 요청
     * @param tokenDto dto
     * @return response entity
     */
    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissue( @RequestBody TokenDto tokenDto) {
        return ResponseEntity.status(200).body(userService.refreshToken(tokenDto));
    }

}
