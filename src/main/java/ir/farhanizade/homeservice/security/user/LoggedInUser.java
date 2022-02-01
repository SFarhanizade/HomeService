package ir.farhanizade.homeservice.security.user;

import ir.farhanizade.homeservice.entity.user.UserStatus;
import ir.farhanizade.homeservice.exception.AccountIsLockedException;
import ir.farhanizade.homeservice.exception.BadEntryException;
import ir.farhanizade.homeservice.exception.EntityNotFoundException;
import ir.farhanizade.homeservice.exception.UserNotLoggedInException;
import ir.farhanizade.homeservice.security.ApplicationUserRole;
import ir.farhanizade.homeservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class LoggedInUser {

    private UserService userService;
    private static UserService service;

    @Autowired
    public void setUserService(UserService userService) {
        service = userService;
    }

    public static UserPrincipalAndAuthorities get() throws UserNotLoggedInException, AccountIsLockedException, EntityNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            throw new UserNotLoggedInException("User Not Logged In!");

        Object principal = authentication.getPrincipal();
        if (principal == null) throw new UserNotLoggedInException("User Not Logged In!");

        UserStatus status = service.getStatusByUsername(principal.toString());

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        if (!UserStatus.ACCEPTED.equals(status))
            throw new AccountIsLockedException("Account is Locked!");
        return new UserPrincipalAndAuthorities(principal.toString(), authorities);
    }

    public static Long id() throws UserNotLoggedInException, EntityNotFoundException, BadEntryException, AccountIsLockedException {
        String username = get().getUsername();
        return service.convertUsername2Id(username);
    }

    public static UserTypeAndId getTypeAndId() throws UserNotLoggedInException, BadEntryException, EntityNotFoundException, AccountIsLockedException {
        String roleStr = get().getAuthorities().stream()
                .filter(a -> a.getAuthority().startsWith("ROLE_"))
                .findFirst()
                .orElseThrow(() -> new BadEntryException("User Not Allowed!"))
                .getAuthority()
                .substring(5);
        ApplicationUserRole role = ApplicationUserRole.valueOf(roleStr);
        return new UserTypeAndId(role, id());
    }

    public static UserStatus getStatus() throws UserNotLoggedInException, BadEntryException, EntityNotFoundException, AccountIsLockedException {
        Long id = id();
        return service.getStatusById(id);
    }
}
