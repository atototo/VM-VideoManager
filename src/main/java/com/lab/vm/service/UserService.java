package com.lab.vm.service;


import com.lab.vm.common.exception.PasswordConfirmFailedException;
import com.lab.vm.common.exception.RefreshTokenException;
import com.lab.vm.common.exception.UserAlreadyExistException;
import com.lab.vm.common.exception.UserNotFoundException;
import com.lab.vm.common.security.SecurityUtils;
import com.lab.vm.common.security.jwt.TokenProvider;
import com.lab.vm.model.domain.Authority;
import com.lab.vm.model.domain.RefreshToken;
import com.lab.vm.model.domain.User;
import com.lab.vm.model.domain.Video;
import com.lab.vm.model.dto.LoginDto;
import com.lab.vm.model.dto.RegisterDto;
import com.lab.vm.model.dto.TokenDto;
import com.lab.vm.model.vo.ApiResponseMessage;
import com.lab.vm.repository.RefreshTokenRepository;
import com.lab.vm.repository.UserRepository;
import com.lab.vm.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;


/**
 * packageName : com.lab.vm.service
 * fileName : UserService
 * author : isbn8
 * date : 2022-01-19
 * description : 유저 관련 서비스
 * ===========================================================
 * DATE                  AUTHOR                  NOTE
 * -----------------------------------------------------------
 * 2022-01-19              isbn8             최초 생성
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;
    private final VideoRepository videoRepository;


    /**
     * Gets user with authorities.
     * 사용자, 권한 정보 조회
     * @return the user with authorities
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities() {

        Optional<User> user = SecurityUtils.getCurrentUsername().
                flatMap(userRepository::findOneWithAuthoritiesByUsername);

        if(SecurityUtils.isAdminAuthority()){
            log.info("[사용자 admin 권한 확인 ] :: {}", user.toString());
            List<Video> videoList = videoRepository.findAll();
            user.orElseThrow().setVideos(videoList);
        }
        log.info("[사용자 정보 확인 ] :: {}", user.toString());

        return user;
    }


    /**
     * Register user api response message.
     * 정보검증부터 사용자 등록
     * @param registerDto the register dto
     * @return the api response message
     */
    @Transactional
    public ApiResponseMessage registerUser(RegisterDto registerDto) {

        //기존 회원 여부 확인 ->  Exception 보내기
        chkValidateExistUser(registerDto);

        //비밀번호 재확인 검증
        chkPasswordConfirm(registerDto);

        var user = userJoin(registerDto);

        var apiResponse = new ApiResponseMessage(HttpStatus.BAD_REQUEST.value(), "회원등록에 실패하였습니다");

        if (user.isPresent()){
            apiResponse = new ApiResponseMessage(HttpStatus.OK.value(), "회원등록에 성공하였습니다");
        }

        return  apiResponse;
    }

    /**
     * User join optional.
     * 사용자 등록
     * @param registerDto the register dto
     * @return the optional
     */
    public Optional<User> userJoin(RegisterDto registerDto) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        //Dto 로 받아온 사용자 정보 Entity 변경
        var user = User.builder()
                .activated(true)
                .username(registerDto.getUsername())
                .password(bCryptPasswordEncoder.encode(registerDto.getPassword()))
                .phone(registerDto.getPhone())
                .email(registerDto.getEmail())
                .build();

        HashSet<Authority> authorityHashSet = new HashSet<>();
        if(user.getUsername().contains("admin")) {
            authorityHashSet.add(new Authority("ROLE_ADMIN"));
            authorityHashSet.add(new Authority("ROLE_USER"));
        } else {
            authorityHashSet.add(new Authority("ROLE_USER"));
        }
        user.setAuthorities(authorityHashSet);

        userRepository.save(user);

        return Optional.of(user);
    }


    /**
     * User modify token dto.
     * 사용자 정보 업데이트
     * @param registerDto the register dto
     * @return the token dto
     */
    @Transactional
    public TokenDto userModify(RegisterDto registerDto){
        //비밀번호 재확인 검증
        chkPasswordConfirm(registerDto);

        //비밀번호 암호화
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        //기존정보 조회
        var user = userRepository.findAllByUsername(registerDto.getUsername());
        //새로운 정보로 저장
        user.ifPresent(selectUser ->{
            selectUser.setPassword(bCryptPasswordEncoder.encode(registerDto.getPassword()));
            selectUser.setPhone(registerDto.getPhone());
            selectUser.setEmail(registerDto.getEmail());
            userRepository.save(selectUser);
        });

        //비밀번호 변경 가능성 -> 토큰 정보 업데이트 후 리턴
        return updateUserToken(registerDto.getUsername(), registerDto.getPassword());
    }


    /**
     * Update user token token dto.
     * 토큰 정보 업데이트
     * @param username the username
     * @param password the password
     * @return the token dto
     */
    public TokenDto updateUserToken(String username, String password) {
       // context 사용자 권한 정보 조회
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //  accessToken refreshToken 생성
        var tokenDto = tokenProvider.createToken(authentication);

        // RefreshToken 업데이트
        var refreshToken = refreshTokenRepository.findByKey(username);

        //새로운 정보로 저장
        refreshToken.ifPresent(selectToken ->{
          selectToken.setToken(tokenDto.getRefreshToken());
            refreshTokenRepository.save(selectToken);
        });

        return tokenDto;
    }


    /**
     * methodName : userDelete
     * author : yelee
     * description : 사용자 탈퇴 요청 시 비활성화 처리
     * @param loginDto dto
     * @return optional
     */
    @Transactional
    public Optional<User> userDelete(LoginDto loginDto){
        // 로그인 정보 검증 후
        var registerDto = findUserInfoByName(loginDto.getUsername());
        //사용자 정보 확인 불가
        if (registerDto == null) {
            throw new UserNotFoundException();
        }
        //입력받은 비밀번호와 암호화된 비밀번호 확인
        if (!isSameOriPwdWithEncPwd(loginDto.getPassword(), registerDto.getPassword())){
            throw new PasswordConfirmFailedException();
        }

        // 삭제 진행 -> 활성화 false

        //기존정보 조회
        var user = userRepository.findAllByUsername(registerDto.getUsername());
        //새로운 정보로 저장
        user.ifPresent(selectUser ->{
            selectUser.setActivated(false);
            userRepository.save(selectUser);
        });

        return user;
    }

    /**
     * chkValidateExistUser
     * 기존 회원 여부 확인
     * @param registerDto registerDto
     */
    private void chkValidateExistUser(RegisterDto registerDto) {

        Optional<User> findUser = userRepository.findOneWithAuthoritiesByUsername(registerDto.getUsername());

        // 기존 회원 여부 검증
        if (findUser.isPresent()) {
            throw new UserAlreadyExistException("이미 등록된 회원입니다.");
        }
    }

    /**
     * chkPasswordConfirm
     * 비밀번호 입력 검증
     * @param registerDto registerDto
     */
    private void chkPasswordConfirm(RegisterDto registerDto) {
        //  비밀번호 입력 검증
        if(!registerDto.getPassword().equals(registerDto.getPasswordConfirm())) {
            throw new PasswordConfirmFailedException("입력된 비밀번호가 다릅니다. 다시 입력해주세요");
        }
    }


    /**
     * Is same ori pwd with enc pwd boolean.
     * 입력된 비밀번호와 DB 비밀번호 동일 여부 확인
     * @param oriPassword the ori password
     * @param encPassword the enc password
     * @return the boolean
     */
    public boolean isSameOriPwdWithEncPwd(String oriPassword, String encPassword){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.matches(oriPassword, encPassword);
    }


    /**
     * 유저 이름으로 사용자 정보 조회
     * @param userName userName
     * @return RegisterDto
     */
    public RegisterDto findUserInfoByName(String userName) {
        return userRepository.findUserInfoByName(userName);
    }

    @Transactional
    public TokenDto refreshToken(TokenDto tokenDto) {
        // AccessToken 에서 Username (pk) 가져오기
        String accessToken = tokenDto.getAccessToken();
        Authentication authentication = tokenProvider.getAuthentication(accessToken);

        // user pk로 유저 검색 / repo 에 저장된 Refresh Token 이 없음
        RegisterDto registerDto =findUserInfoByName(authentication.getName());

        RefreshToken refreshToken = refreshTokenRepository.findByKey(registerDto.getUsername())
                .orElseThrow(RefreshTokenException::new);


        // 만료된 refresh token 에러
        if (!tokenProvider.validateToken(refreshToken.getToken())) {
            throw new RefreshTokenException();
        }

        // AccessToken, RefreshToken 토큰 재발급, 리프레쉬 토큰 저장
        TokenDto newCreatedToken = tokenProvider.createToken(authentication);
        RefreshToken updateRefreshToken = refreshToken.updateToken(newCreatedToken.getRefreshToken());
        refreshTokenRepository.save(updateRefreshToken);
        return newCreatedToken;
    }

}
