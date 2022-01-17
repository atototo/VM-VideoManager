package com.lab.vm.service;


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
@Transactional  //빼야되는지 확인하기
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
    @Transactional
    public ApiResponseMessage registerUser(RegisterDto registerDto) {

        //기존 회원 여부 확인 ->  Exception 보내기
        chkValidateUser(registerDto);

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
    @Transactional
    public Optional<User> userModify(RegisterDto registerDto){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();


        //Dto 로 받아온 사용자 정보 Entity 변경
//      var user = User.builder()
//         .id(registerDto.getId())
//         .activated(true)
//         .username(registerDto.getUsername())
//         .password(bCryptPasswordEncoder.encode(registerDto.getPassword()))
//         .phone(registerDto.getPhone())
//         .email(registerDto.getEmail())
//         .build();

        var user = userRepository.findAllByUsername(registerDto.getUsername());


        user.ifPresent(selectUser ->{
//         selectUser.setActivated(true);
            selectUser.setPassword(bCryptPasswordEncoder.encode(registerDto.getPassword()));
            selectUser.setPhone(registerDto.getPhone());
            selectUser.setEmail(registerDto.getEmail());
            User newUser = userRepository.save(selectUser);
        });
        return user;
    }



    /**
     * 중복사용자 검증
     * @param registerDto
     */
    private void chkValidateUser(RegisterDto registerDto) {

        Optional<User> findUser = userRepository.findOneWithAuthoritiesByUsername(registerDto.getUsername());

        if (findUser.isPresent()) {
            throw new RuntimeException("이미 등록된 회원입니다.");
        }

    }


}
