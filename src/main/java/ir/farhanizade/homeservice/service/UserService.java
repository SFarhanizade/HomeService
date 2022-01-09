package ir.farhanizade.homeservice.service;


import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.in.UserPasswordInDto;
import ir.farhanizade.homeservice.dto.in.UserSearchInDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.dto.out.UserSearchOutDto;
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

    public EntityOutDto save(UserInDto user) throws DuplicateEntityException, NameNotValidException, EmailNotValidException, PasswordNotValidException, UserNotValidException, NullFieldException {
        EntityOutDto result = new EntityOutDto();
        if ("expert".equals(user.getType())) {
            result = expertService.save(user);

        } else if ("customer".equals(user.getType())) {
            result = customerService.save(user);
        }
        return result;
    }

    public List<UserSearchOutDto> search(UserSearchInDto user) {
        List<UserSearchOutDto> result = new ArrayList<>();
        if ("expert".equals(user.getType())) {
            result = expertService.search(user);
        } else if("customer".equals(user.getType())){
            result = customerService.search(user);
        }
        return result;
    }
}
