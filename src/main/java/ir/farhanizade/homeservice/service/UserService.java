package ir.farhanizade.homeservice.service;


import ir.farhanizade.homeservice.dto.in.UserPasswordInDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.entity.user.User;
import ir.farhanizade.homeservice.exception.PasswordNotValidException;
import ir.farhanizade.homeservice.exception.WrongPasswordException;
import ir.farhanizade.homeservice.repository.user.UserRepository;
import ir.farhanizade.homeservice.service.util.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    @Transactional
    public EntityOutDto changePassword(UserPasswordInDto user) throws PasswordNotValidException, WrongPasswordException {
        String currentPassword = user.getCurrentPassword();
        String newPassword = user.getNewPassword();
        Long id = user.getId();
        if(currentPassword.equals(newPassword)){
            throw new PasswordNotValidException("The new password is the same as the current password!");
        }
        if(!Validation.passwordIsValid(newPassword)){
            throw new PasswordNotValidException("The new password is not valid!");
        }
        User byIdAndPass = repository.isCorrectByPassword(id, currentPassword);
        if(byIdAndPass==null){
            throw new WrongPasswordException("The current password is not correct!");
        }
        repository.updatePassword(id, newPassword);


        return new EntityOutDto(id);
    }
}
