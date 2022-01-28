package ir.farhanizade.homeservice.security.user;

import ir.farhanizade.homeservice.exception.BadEntryException;
import ir.farhanizade.homeservice.exception.EntityNotFoundException;
import ir.farhanizade.homeservice.exception.UserNotLoggedInException;
import ir.farhanizade.homeservice.security.ApplicationUserRole;
import ir.farhanizade.homeservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class LoggedInUser {

    private UserService userService;
    private static UserService service;

    @Autowired
    public void setUserService(UserService userService) {
        service = userService;
    }

    public static UserDetails get() throws UserNotLoggedInException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            throw new UserNotLoggedInException("User Not Logged In!");
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal);
        } else {
            throw new UserNotLoggedInException("User Not Logged In!");
        }
    }

    public static Long id() throws UserNotLoggedInException, EntityNotFoundException, BadEntryException {
        String username = get().getUsername();
        return service.convertUsername2Id(username);
    }

    public static UserTypeAndId getTypeAndId() throws UserNotLoggedInException, BadEntryException, EntityNotFoundException {
        String roleStr = get().getAuthorities().stream()
                .filter(a -> a.getAuthority().startsWith("ROLE_"))
                .findFirst()
                .orElseThrow(() -> new BadEntryException("User Not Allowed!"))
                .getAuthority()
                .substring(5);
        ApplicationUserRole role = ApplicationUserRole.valueOf(roleStr);
        return new UserTypeAndId(role,id());
    }
}
