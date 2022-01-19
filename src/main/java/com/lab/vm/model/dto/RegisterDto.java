package com.lab.vm.model.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;


/**
 * packageName : com.lab.vm.model.dto.RegisterDto
 * fileName : RegisterDto
 * author : yelee
 * date : 2022-01-18
 * description : 회원 등록 및 수정 input 정보 dto
 * ===========================================================
 * DATE                  AUTHOR                  NOTE
 * -----------------------------------------------------------
 * 2022-01-18              yelee             최초 생성
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RegisterDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank
    @Length(min = 3,  message="이름은 최소 3글자 이상입니다!!")
    private String username;

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Length(min = 4, max=20,message = "비밀번호는 5글자 20글자 이하입니다.")
    private String password;

    //    @Transient  //@Transient 엔티티 객체의 데이터와 테이블의 컬럼(column)과 매핑하고 있는 관계를 제외하기 위해 사용
   @NotBlank(message = "비밀번호 재확인을 입력해주세요")
   private String passwordConfirm;

    @NotBlank(message = "회원 이메일은 필수 입니다")
    @Email
    private String email;

    @Pattern(regexp="^01(\\d)(?:\\d{3}|\\d{4})\\d{4}$", message="올바른 전화번호를 입력해주세요!!")
    private String phone;

    private Boolean rememberMe;


    public RegisterDto(Long id, String password, String username, String email, String phone) {
        this.id = id;
        this.password = password;
        this.username = username;
        this.email = email;
        this.phone = phone;
    }

}