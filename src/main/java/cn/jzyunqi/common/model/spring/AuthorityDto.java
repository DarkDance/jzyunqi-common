package cn.jzyunqi.common.model.spring;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public class AuthorityDto implements GrantedAuthority {
    private static final long serialVersionUID = 7473503554069342800L;

    private String privilege;

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    @Override
    public String getAuthority() {
        return privilege;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof AuthorityDto) {
            return privilege.equals(((AuthorityDto) obj).privilege);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.privilege.hashCode();
    }

    @Override
    public String toString() {
        return this.privilege;
    }

}
