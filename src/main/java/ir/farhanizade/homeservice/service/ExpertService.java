package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.entity.user.UserStatus;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.repository.user.ExpertRepository;
import ir.farhanizade.homeservice.service.util.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpertService {
    private final ExpertRepository repository;

    @Transactional
    public void save(Expert expert) throws NameNotValidException, EmailNotValidException, PasswordNotValidException, UserNotValidException, DuplicateEntityException {
        boolean isValid = false;
        isValid = Validation.isValid(expert);

        if(!isValid)
            throw new UserNotValidException("");
        String email = expert.getEmail();
        Expert byEmail = repository.findByEmail(email);
        boolean finalCheck = byEmail!=null && expert.getId()==null;
        if(finalCheck)
            throw new DuplicateEntityException("");
        repository.save(expert);
    }

    public Expert findByEmail(String email){
        if(email==null)
            throw new IllegalStateException("Null Email");
        return repository.findByEmail(email);
    }

    public List<Expert> findByCredit(BigDecimal credit){
        return repository.findByCredit(credit);
    }

    public List<Expert> findByStatus(UserStatus status){
        return repository.findByStatus(status);
    }

    public List<Expert> findByExpertise(SubService service){
        return repository.findByExpertise(service.getId());
    }
}
