package com.jy.rock.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jy.rock.domain.Group;
import com.jy.rock.domain.User;
import lombok.Data;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author hzhou
 */
@Data
@ToString(callSuper = true)
public class UserWithGroupsVO extends User implements UserDetails {

    private String fullName;

    private List<Group> groups;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.groups == null ? Collections.emptyList()
                : this.groups.stream().map(i -> new SimpleGrantedAuthority(i.getName())).collect(Collectors.toList());
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return super.getPassword();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !this.getNeedChangePasswordWhenLogin();
    }

    @Override
    public boolean isEnabled() {
        return this.getEnable();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserWithGroupsVO) {
            return Objects.equals(((UserWithGroupsVO) obj).getId(), this.getId());
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getId()) ^ Objects.hashCode(this.getPassword());
    }
}
