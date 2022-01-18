package com.lab.vm.service;


import com.lab.vm.common.exception.PasswordConfirmFailedException;
import com.lab.vm.common.exception.UserAlreadyExistException;
import com.lab.vm.common.security.SecurityUtils;
import com.lab.vm.model.domain.Authority;
import com.lab.vm.model.domain.User;
import com.lab.vm.model.dto.RegisterDto;
import com.lab.vm.model.vo.ApiResponseMessage;
import com.lab.vm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities() {

        Optional<User> user = SecurityUtils.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByUsername);
        log.info("[사용자 정보 확인 ] :: {}", user.toString());

        return user;
    }


    /**
     * 사용자 입력 정보 검증 및 등록
     * @param registerDto
     * @return
     */
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
     * 검증 통과 된 사용자 정보 회원 등록
     * @param registerDto
     * @return
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
            authorityHashSet.add(new Authority("ROLE_UPLOAD"));
        }
        user.setAuthorities(authorityHashSet);

        userRepository.save(user);

        return Optional.of(user);
    }


    /**
     * 사용자 입력 정보 검증 및 등록
     * @param registerDto
     * @return
     */
    @Transactional
    public ApiResponseMessage modifyUser(RegisterDto registerDto) {

        chkPasswordConfirm(registerDto);

        var user = userModify(registerDto);

        var apiResponse = new ApiResponseMessage(HttpStatus.BAD_REQUEST.value(), "회원 정보 수정에 실패하였습니다");

        if (user.isPresent()){
            apiResponse = new ApiResponseMessage(HttpStatus.OK.value(), "회원 정보 수정에 성공하였습니다");

        }

        return  apiResponse;
    }


    /**
     * 회원정보 수정
     * @param registerDto
     * @return
     */
    public Optional<User> userModify(RegisterDto registerDto){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        var user = userRepository.findAllByUsername(registerDto.getUsername());
        user.ifPresent(selectUser ->{
            selectUser.setPassword(bCryptPasswordEncoder.encode(registerDto.getPassword()));
            selectUser.setPhone(registerDto.getPhone());
            selectUser.setEmail(registerDto.getEmail());
            userRepository.save(selectUser);
        });
        return user;
    }



    /**
     * 중복사용자 검증
     * @param registerDto
     */
    private void chkValidateExistUser(RegisterDto registerDto) {

        Optional<User> findUser = userRepository.findOneWithAuthoritiesByUsername(registerDto.getUsername());

        // 기존 회원 여부 검증
        if (findUser.isPresent()) {
            throw new UserAlreadyExistException("이미 등록된 회원입니다.");
        }
    }

    /**
     * 비밀번호 재확인 검증
     * @param registerDto
     */
    private void chkPasswordConfirm(RegisterDto registerDto) {
        //  비밀번호 입력 검증
        if(!registerDto.getPassword().equals(registerDto.getPasswordConfirm())) {
            throw new PasswordConfirmFailedException("입력된 비밀번호가 다릅니다. 다시 입력해주세요");
        }
    }


}
