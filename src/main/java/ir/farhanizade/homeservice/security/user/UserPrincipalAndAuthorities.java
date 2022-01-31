package ir.farhanizade.homeservice.security.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
@AllArgsConstructor
public class UserPrincipalAndAuthorities {
    private String username;
    private Collection<? extends GrantedAuthority> authorities;
}
