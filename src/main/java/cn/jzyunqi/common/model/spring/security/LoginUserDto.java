package cn.jzyunqi.common.model.spring.security;

import cn.jzyunqi.common.model.spring.AuthorityDto;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public class LoginUserDto implements UserDetails {

    private static final long serialVersionUID = -4774617466644201380L;

    private Long id;

    private String password;

    private String username;

    private String loginUsername;

    private List<AuthorityDto> authorities;

    private List<Long> authorityIds;

    private boolean accountNonExpired;

    private boolean accountNonLocked;

    private boolean credentialsNonExpired;

    private boolean enabled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLoginUsername() {
        return loginUsername;
    }

    public void setLoginUsername(String loginUsername) {
        this.loginUsername = loginUsername;
    }

    @Override
    public List<AuthorityDto> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<AuthorityDto> authorities) {
        this.authorities = authorities;
    }

    public List<Long> getAuthorityIds() {
        return authorityIds;
    }

    public void setAuthorityIds(List<Long> authorityIds) {
        this.authorityIds = authorityIds;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
