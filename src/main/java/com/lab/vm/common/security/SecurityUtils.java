package com.lab.vm.common.security;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.management.relation.Role;
import java.util.Optional;

@Slf4j
public class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * Get the login of the current user.
     * 현재 컨텍스트의 사용자 정보 반환 한다.
     * @return the login of the current user.
     */
    public static Optional<String> getCurrentUsername() {
        //현재 컨텍스트에서 사용자 정보 가져온다
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.debug("no authentication in security context found");
            return Optional.empty();
        }

        String username = null;
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
            username = springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            username = (String) authentication.getPrincipal();
        }

        log.info("found username '{}' in security context", username);

        return Optional.ofNullable(username);
    }


    /**
     * 사용자 admin 권한 확인
     * @return boolean
     */
    public static boolean isAdminAuthority(){
        //현재 컨텍스트에서 사용자 정보 가져온다
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //권한정보 확인 후에 사용될 로직인기 때문에 null 체크 필요 없다.
        UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();

        for (GrantedAuthority authority : springSecurityUser.getAuthorities()) {
            if (authority.toString().equals("ROLE_ADMIN")) {
                log.info("사용자 admin 권한 확인 됨");
                return true;
            }
        }
        return false;
    }
}
