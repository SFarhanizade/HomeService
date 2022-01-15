package ir.farhanizade.homeservice.service;


import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.in.UserPasswordInDto;
import ir.farhanizade.homeservice.dto.in.UserSearchInDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.dto.out.UserSearchOutDto;
import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.entity.user.User;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.repository.user.UserRepository;
import ir.farhanizade.homeservice.service.util.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final ExpertService expertService;
    private final CustomerService customerService;
    private final AdminService adminService;

    @Transactional(rollbackFor = Exception.class)
    public EntityOutDto changePassword(UserPasswordInDto user) throws PasswordNotValidException, WrongPasswordException {
        String currentPassword = user.getCurrentPassword();
        String newPassword = user.getNewPassword();
        Long id = user.getId();
        Optional<User> byId = repository.findById(id);
        User entity = byId.get();
        if (currentPassword.equals(newPassword)) {
            throw new PasswordNotValidException("The new password is the same as the current password!");
        }
        if (!Validation.passwordIsValid(newPassword)) {
            throw new PasswordNotValidException("The new password is not valid!");
        }

        if (currentPassword.equals(entity.getPassword())) {
            throw new WrongPasswordException("The current password is not correct!");
        }
        entity.setPassword(newPassword);
        repository.save(entity);
        return new EntityOutDto(id);
    }

    public EntityOutDto save(UserInDto user, Class<?> type) throws DuplicateEntityException, NameNotValidException, EmailNotValidException, PasswordNotValidException, UserNotValidException, NullFieldException {
        EntityOutDto result;
        if (type == Expert.class) {
            result = expertService.save(user);

        } else if (type == Customer.class) {
            result = customerService.save(user);
        } else{
            result = adminService.save(user);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<UserSearchOutDto> search(UserSearchInDto user) {
        List<UserSearchOutDto> result;
        if ("expert".equals(user.getType())) {
            result = expertService.search(user);
        } else if ("customer".equals(user.getType())) {
            result = customerService.search(user);
        } else {
            result = searchUser(user);
        }
        return result;
    }

    @Transactional(readOnly = true)
    List<UserSearchOutDto> searchUser(UserSearchInDto user) {
        List<User> searchResult = repository.search(user);
        List<UserSearchOutDto> result = searchResult.stream()
                .map(e -> new UserSearchOutDto().convert2Dto(e))
                .peek(e -> e.setType("user"))
                .toList();
        return result;
    }
}
