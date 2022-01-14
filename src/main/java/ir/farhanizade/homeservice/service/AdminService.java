package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.entity.user.Admin;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.repository.user.AdminRepository;
import ir.farhanizade.homeservice.service.util.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository repository;

    @Transactional
    public EntityOutDto save(UserInDto user) throws NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, UserNotValidException, DuplicateEntityException {
        Admin admin = user.convert2Admin();
        if(!Validation.isValid(admin)) throw new UserNotValidException("User is not valid!");
        if(finalCheck(admin)) throw new DuplicateEntityException("User exists!");
        Admin result = repository.save(admin);
        return new EntityOutDto(result.getId());
    }

    @Transactional(readOnly = true)
    boolean finalCheck(Admin admin) {
        String email = admin.getEmail();
        Admin byEmail = repository.findByEmail(email);
        return byEmail != null && admin.getId() == null;
    }
}
