package cn.jzyunqi.common.utils;

import cn.jzyunqi.common.exception.BusinessException;
import cn.jzyunqi.common.model.spring.security.LoginUserDto;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public class CurrentUserUtils {
    private CurrentUserUtils() {

    }

    /**
     * 获取当前登录的用户ID，如果未登录，抛出异常
     *
     * @return 用户ID
     */
    public static Long currentUserId() throws BusinessException {
        LoginUserDto loginUserDto = getCurrUser();
        if (loginUserDto == null) {
            throw new BusinessException("找不到指定用户");
        }
        return loginUserDto.getId();
    }

    /**
     * 获取当前登录的用户ID，如果未登录，返回null
     *
     * @return 用户ID
     */
    public static Long currentUserIdWithNull() {
        LoginUserDto loginUserDto = getCurrUser();
        if (loginUserDto == null) {
            return null;
        }
        return loginUserDto.getId();
    }

    /**
     * 获取当前登录的用户，如果未登录，抛出异常
     *
     * @return 用户
     */
    public static LoginUserDto currentUser() throws BusinessException {
        LoginUserDto loginUserDto = getCurrUser();
        if (loginUserDto == null) {
            throw new BusinessException("找不到指定用户");
        }
        return loginUserDto;
    }

    /**
     * 获取当前用户
     *
     * @param <T> User
     * @return 当前用户
     */
    @SuppressWarnings("unchecked")
    private static <T extends UserDetails> T getCurrUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        } else {
            return (T) authentication.getPrincipal();
        }
    }
}
