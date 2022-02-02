package ir.farhanizade.homeservice.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static ir.farhanizade.homeservice.security.ApplicationUserPermission.*;

public enum ApplicationUserRole {
    ADMIN(Set.of(ApplicationUserPermission.values())),
    EXPERT(Set.of(EXPERT_WRITE, EXPERT_READ, ORDER_READ, SUGGESTION_WRITE, SUGGESTION_READ, CREDIT_WRITE, CREDIT_READ, TRANSACTION_READ, COMMENT_READ)),
    CUSTOMER(Set.of(CUSTOMER_WRITE, CUSTOMER_READ, ORDER_WRITE, ORDER_READ, SUGGESTION_READ, CREDIT_WRITE, CREDIT_READ, TRANSACTION_WRITE, TRANSACTION_READ, COMMENT_WRITE, COMMENT_READ));

    private final Set<ApplicationUserPermission> permissions;

    ApplicationUserRole(Set<ApplicationUserPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<? extends GrantedAuthority> getPermissions() {
        Set<SimpleGrantedAuthority> authorities = permissions.stream()
                .map(p -> new SimpleGrantedAuthority(p.name()))
                .collect(Collectors.toSet());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
