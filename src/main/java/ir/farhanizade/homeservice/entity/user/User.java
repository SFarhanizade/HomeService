package ir.farhanizade.homeservice.entity.user;

import ir.farhanizade.homeservice.entity.Transaction;
import ir.farhanizade.homeservice.entity.core.BasePerson;
import ir.farhanizade.homeservice.security.ApplicationUserRole;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

//@EqualsAndHashCode(callSuper = true)
//@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class User extends BasePerson implements UserDetails {

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<ApplicationUserRole> roles;

    @Column(nullable = false)
    @Builder.Default
    private Date dateTime = new Date(System.currentTimeMillis());

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal credit = new BigDecimal(0);

    @Builder.Default
    @Column(nullable = false)
    private UserStatus status = UserStatus.NEW;
    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (ApplicationUserRole r : roles) {
            authorities.addAll(r.getPermissions());
        }
        return authorities;
    }

    @Override
    public String getUsername() {
        return getEmail();//TODO: Continue here and complete the UserDetails implementations
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
