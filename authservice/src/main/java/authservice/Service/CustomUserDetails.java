package authservice.Service;

import authservice.entities.UserInfo;
import authservice.entities.UserRoles;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails extends UserInfo implements UserDetails {

    private String username;
    private String password;
    Collection<? extends GrantedAuthority> authorities;

    CustomUserDetails(UserInfo byUsername){
        this.username=byUsername.getUsername();
        this.password=byUsername.getPassword();
        List<GrantedAuthority> auth= new ArrayList<>();
        for(UserRoles role:byUsername.getRoles()){
           auth.add(new SimpleGrantedAuthority(role.getRoleName().toUpperCase()));
        }
        this.authorities=auth;
    }

    @Override
    public String getPassword(){
        return password;
    }
    @Override
    public String getUsername(){
        return username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
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
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
