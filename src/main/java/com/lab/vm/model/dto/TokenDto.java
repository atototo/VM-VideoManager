package com.lab.vm.model.dto;

import lombok.*;

/**
 * packageName : com.lab.vm.model.dto
 * fileName : TokenDto
 * author : yelee
 * date : 2022-01-18
 * description : 토큰 정보 dto
 * ===========================================================
 * DATE                  AUTHOR                  NOTE
 * -----------------------------------------------------------
 * 2022-01-18              yelee             최초 생성
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {

    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpireDate;
}
