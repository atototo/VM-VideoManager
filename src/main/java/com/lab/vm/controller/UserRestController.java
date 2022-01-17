package com.lab.vm.controller;


import com.lab.vm.common.security.jwt.JWTFilter;
import com.lab.vm.common.security.jwt.TokenProvider;
import com.lab.vm.model.domain.User;
import com.lab.vm.model.dto.RegisterDto;
import com.lab.vm.model.vo.ApiResponseMessage;
import com.lab.vm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;


    @GetMapping("/user")
    public ResponseEntity<User> getActualUser() {
        return ResponseEntity.ok(userService.getUserWithAuthorities().get());
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponseMessage> registerUser(@RequestBody @Valid RegisterDto registerDto) {
        return ResponseEntity.ok(userService.registerUser(registerDto));
    }

    @PutMapping("/modify-user")
    public ResponseEntity<?> modifyUser(@RequestBody @Valid RegisterDto registerDto){

        ApiResponseMessage apiResponseMessage = userService.modifyUser(registerDto);

        if (apiResponseMessage.getStatus() == HttpStatus.OK.value()) {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(registerDto.getUsername(), registerDto.getPassword());

            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            boolean rememberMe = (registerDto.getRememberMe() == null) ? false : registerDto.getRememberMe();
            String jwt = tokenProvider.createToken(authentication, rememberMe);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

            return new ResponseEntity<>(new AuthenticationRestController.JWTToken(jwt), httpHeaders, HttpStatus.OK);
        }
        return ResponseEntity.ok(apiResponseMessage);
    }
}
