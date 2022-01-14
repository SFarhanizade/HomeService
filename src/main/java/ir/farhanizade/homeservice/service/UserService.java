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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final ExpertService expertService;
    private final CustomerService customerService;
    private final AdminService adminService;

    @Transactional
    public EntityOutDto changePassword(UserPasswordInDto user) throws PasswordNotValidException, WrongPasswordException {
        String currentPassword = user.getCurrentPassword();
        String newPassword = user.getNewPassword();
        Long id = user.getId();
        if (currentPassword.equals(newPassword)) {
            throw new PasswordNotValidException("The new password is the same as the current password!");
        }
        if (!Validation.passwordIsValid(newPassword)) {
            throw new PasswordNotValidException("The new password is not valid!");
        }
        User byIdAndPass = repository.isCorrectByPassword(id, currentPassword);
        if (byIdAndPass == null) {
            throw new WrongPasswordException("The current password is not correct!");
        }
        repository.updatePassword(id, newPassword);


        return new EntityOutDto(id);
    }

    public EntityOutDto save(UserInDto user, Class<?> type) throws DuplicateEntityException, NameNotValidException, EmailNotValidException, PasswordNotValidException, UserNotValidException, NullFieldException {
        EntityOutDto result = new EntityOutDto();
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
