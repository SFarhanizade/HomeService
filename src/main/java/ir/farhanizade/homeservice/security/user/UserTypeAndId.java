package ir.farhanizade.homeservice.security.user;

import ir.farhanizade.homeservice.security.ApplicationUserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserTypeAndId {
    private ApplicationUserRole role;
    private Long id;
}
