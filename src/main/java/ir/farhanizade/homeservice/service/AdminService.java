package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.in.UserSearchInDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.dto.out.UserSearchOutDto;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.user.Admin;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.repository.user.AdminRepository;
import ir.farhanizade.homeservice.security.ApplicationUserRole;
import ir.farhanizade.homeservice.security.user.LoggedInUser;
import ir.farhanizade.homeservice.service.util.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository repository;
    private final ExpertService expertService;
    private final CustomerService customerService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;


    @Transactional(rollbackFor = Exception.class)
    public EntityOutDto save(UserInDto user) throws NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, UserNotValidException, DuplicateEntityException {
        Admin admin = user.convert2Admin();
        if (!Validation.isValid(admin)) throw new UserNotValidException("User is not valid!");
        if (finalCheck(admin)) throw new DuplicateEntityException("User exists!");
        admin.setRoles(Set.of(ApplicationUserRole.ADMIN));
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        Validation.enableUser(admin);
        Admin result = repository.save(admin);
        return new EntityOutDto(result.getId());
    }

    @Transactional(readOnly = true)
    boolean finalCheck(Admin admin) {
        String email = admin.getEmail();
        Admin byEmail = repository.findByEmail(email);
        return byEmail != null && admin.getId() == null;
    }

    public EntityOutDto acceptExpert(Long expertId) throws UserNotValidException, EntityNotFoundException, UserNotLoggedInException, BadEntryException {
        return expertService.acceptExpert(expertId);
    }

    public boolean exists(Long id) throws UserNotValidException {
        if (!repository.existsById(id)) throw new UserNotValidException("User doesn't exist!");
        return true;
    }

    @Transactional(readOnly = true)
    public CustomPage<UserSearchOutDto> search(UserSearchInDto user, Pageable pageable) throws EntityNotFoundException, UserNotValidException {
        return userService.search(user, pageable);
    }
}
