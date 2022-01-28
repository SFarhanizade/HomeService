package ir.farhanizade.homeservice.security.user;

import ir.farhanizade.homeservice.repository.user.UserRepository;
import ir.farhanizade.homeservice.security.ApplicationUserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserManager implements UserDetailsService {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserRepository userRepository;

    private List<CustomUser> users = new ArrayList<>(List.of(
            CustomUser.builder()
                    .username("customer")
                    .password(passwordEncoder.encode("password"))
                    .permissions(ApplicationUserRole.CUSTOMER.getPermissions())
                    .isAccountNonExpired(true)
                    .isCredentialsNonExpired(true)
                    .isEnabled(true)
                    .isAccountNonLocked(true)
                    .build(),
            CustomUser.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("password"))
                    .permissions(ApplicationUserRole.ADMIN.getPermissions())
                    .isAccountNonExpired(true)
                    .isCredentialsNonExpired(true)
                    .isEnabled(true)
                    .isAccountNonLocked(true)
                    .build()
    ));

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(""));
        /*return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException(""));*/
    }
}
