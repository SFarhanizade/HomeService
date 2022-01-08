package ir.farhanizade.homeservice.service;


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
    public User changePassword(Long id, String currentPassword, String newPassword) throws PasswordNotValidException, WrongPasswordException {
        if(currentPassword.equals(newPassword)){
            throw new PasswordNotValidException("The new password is the same as the current password!");
        }
        if(!Validation.passwordIsValid(newPassword)){
            throw new PasswordNotValidException("The new password is not valid!");
        }
        User byIdAndPass = repository.isCorrectByPassword(id, currentPassword);
        if(!currentPassword.equals(byIdAndPass.getPassword())){
            throw new WrongPasswordException("The current password is not correct!");
        }
        User result = repository.updatePassword(id, newPassword);
        return result;
    }
}
