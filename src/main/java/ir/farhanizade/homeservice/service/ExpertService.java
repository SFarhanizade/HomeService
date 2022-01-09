package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.in.ExpertAddServiceInDto;
import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.in.UserSearchInDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.dto.out.ExpertAddServiceOutDto;
import ir.farhanizade.homeservice.dto.out.UserSearchOutDto;
import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.entity.user.UserStatus;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.repository.user.ExpertRepository;
import ir.farhanizade.homeservice.service.util.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpertService {
    private final ExpertRepository repository;
    private final SubServiceService serviceManager;

    @Transactional
    public EntityOutDto save(UserInDto user) throws NameNotValidException, EmailNotValidException, PasswordNotValidException, UserNotValidException, DuplicateEntityException, NullFieldException {
        Expert expert = user.convert2Expert();
        if (!Validation.isValid(expert))
            throw new UserNotValidException("User is not valid!");
        if (finalCheck(expert))
            throw new DuplicateEntityException("User exists!");
        Expert result = repository.save(expert);
        return new EntityOutDto(result.getId());
    }

    @Transactional(readOnly = true)
    public Expert findByEmail(String email) {
        if (email == null)
            throw new IllegalStateException("Null Email");
        return repository.findByEmail(email);
    }

    public List<Expert> findByCredit(BigDecimal credit) {
        return repository.findByCredit(credit);
    }

    public List<Expert> findByStatus(UserStatus status) {
        return repository.findByStatus(status);
    }

    public List<Expert> findByExpertise(SubService service) {
        return repository.findByExpertise(service.getId());
    }

    private boolean finalCheck(Expert expert) {
        String email = expert.getEmail();
        Expert byEmail = repository.findByEmail(email);
        return byEmail != null && expert.getId() == null;
    }

    public List<UserSearchOutDto> search(UserSearchInDto user) {
        List<Expert> searchResult = repository.search(user);
        List<UserSearchOutDto> result = searchResult.stream()
                .map(e -> new UserSearchOutDto().convert2Dto(e))
                .peek(e -> e.setType("expert"))
                .toList();
        return result;
    }

    @Transactional
    public ExpertAddServiceOutDto addService(ExpertAddServiceInDto request) throws EntityNotFoundException, DuplicateEntityException {
        Expert expert;
        Optional<Expert> byId = repository.findById(request.getExpertId());
        if (byId.isPresent()) {
            expert = byId.get();
        } else {
            throw new EntityNotFoundException("User doesn't exist!");
        }
        SubService service = serviceManager.loadById(request.getServiceId());
        boolean serviceExists = !expert.addService(service);
        if(serviceExists){
            throw new DuplicateEntityException("The service exists for this expert!");
        }
        Expert saved = repository.save(expert);
        ExpertAddServiceOutDto result = ExpertAddServiceOutDto.builder()
                .expertId(saved.getId())
                .services(expert.getExpertises().stream()
                        .map(s -> new EntityOutDto(s.getId()))
                        .collect(Collectors.toSet())
                )
                .build();
        return result;
    }
}
