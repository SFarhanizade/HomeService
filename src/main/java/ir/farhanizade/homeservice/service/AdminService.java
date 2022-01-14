package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.entity.user.Admin;
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
    private final ExpertService expertService;

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

    public EntityOutDto acceptExpert(Long id, Long expertId) throws UserNotValidException, EntityNotFoundException {
        exists(id);
        return expertService.acceptExpert(expertId);
    }

    public boolean exists(Long id) throws UserNotValidException {
        if(!repository.existsById(id)) throw new UserNotValidException("User doesn't exist!");
        return true;
    }
}
