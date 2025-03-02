//package com.shizzy.moneytransfer.util;
//
//import com.shizzy.moneytransfer.model.Admin;
//import com.shizzy.moneytransfer.model.User;
//import com.shizzy.moneytransfer.model.UserRole;
//import lombok.Getter;
//import org.jetbrains.annotations.NotNull;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.List;
//
//public class CustomUserDetails  implements UserDetails {
//
//    private final String username;
//    @Getter
//    private final Integer id;
//    private final String password;
//    private final boolean accountLocked;
//    private final boolean enabled;
//    Collection<? extends GrantedAuthority> authorities;
//
//    public CustomUserDetails(@NotNull Admin byUsername, Integer id) {
//        this.username = byUsername.getUsername();
//        this.password= byUsername.getPassword();
//        this.accountLocked = byUsername.isAccountLocked();
//        this.enabled = byUsername.isEnabled();
//        this.id = id;
//        List<GrantedAuthority> auths = new ArrayList<>();
//
//        for(UserRole role : byUsername.getRoles()) {
//
//            auths.add(new SimpleGrantedAuthority(role.getName().toUpperCase()));
//        }
//        this.authorities = auths;
//    }
//
//    public CustomUserDetails(@NotNull User user, Integer id) {
//        this.username = user.getEmail();
//        this.password = user.getPassword();
//        this.accountLocked = user.isAccountLocked();
//        this.enabled = user.isEnabled();
//        this.id = id;
//        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return authorities;
//    }
//
//    @Override
//    public String getPassword() {
//        return password;
//    }
//
//    @Override
//    public String getUsername() {
//        return username;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return !accountLocked;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return enabled;
//    }
//
//    public User getUser() {
//        return null;
//    }
//}
