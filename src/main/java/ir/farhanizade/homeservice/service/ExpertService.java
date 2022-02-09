package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.controller.api.filter.UserSpecification;
import ir.farhanizade.homeservice.dto.in.*;
import ir.farhanizade.homeservice.dto.out.*;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.entity.user.UserExpert;
import ir.farhanizade.homeservice.entity.user.UserStatus;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.repository.user.ExpertRepository;
import ir.farhanizade.homeservice.security.ApplicationUserRole;
import ir.farhanizade.homeservice.security.user.LoggedInUser;
import ir.farhanizade.homeservice.service.util.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ir.farhanizade.homeservice.entity.user.UserStatus.ACCEPTED;

@Service
@RequiredArgsConstructor
public class ExpertService {
    private final ExpertRepository repository;
    private final SubServiceService serviceManager;
    private final PasswordEncoder passwordEncoder;


    @Transactional(rollbackFor = Exception.class)
    public Long save(UserInDto user) throws NameNotValidException, EmailNotValidException, PasswordNotValidException, UserNotValidException, DuplicateEntityException, NullFieldException {
        UserExpert expert = user.convert2Expert();
        if (!Validation.isValid(expert))
            throw new UserNotValidException("User is not valid!");
        if (finalCheck(expert))
            throw new DuplicateEntityException("User exists!");
        expert.setRoles(Set.of(ApplicationUserRole.EXPERT));
        expert.setPassword(passwordEncoder.encode(expert.getPassword()));
        Validation.enableUser(expert);
        UserExpert result = repository.save(expert);
        return result.getId();
    }


    @Transactional(rollbackFor = Exception.class)
    public ExpertAddServiceOutDto addService(ExpertAddServiceInDto request) throws EntityNotFoundException, DuplicateEntityException, ExpertNotAcceptedException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
        UserExpert expert = findById(LoggedInUser.id());
        if (!expert.getStatus().equals(ACCEPTED)) throw new ExpertNotAcceptedException("User is not allowed!");
        SubService service = serviceManager.loadById(request.getServiceId());
        boolean serviceExists = !expert.addService(service);
        if (serviceExists) {
            throw new DuplicateEntityException("The service exists for this expert!");
        }
        UserExpert saved = repository.save(expert);
        ExpertAddServiceOutDto result = ExpertAddServiceOutDto.builder()
                .expertId(saved.getId())
                .services(expert.getExpertises().stream()
                        .map(s -> new EntityOutDto(s.getId()))
                        .collect(Collectors.toSet()))
                .build();
        return result;
    }

    @Transactional(readOnly = true)
    public UserExpert findById(Long id) throws EntityNotFoundException {
        Optional<UserExpert> byId = repository.findById(id);
        if (byId.isPresent()) {
            return byId.get();
        } else throw new EntityNotFoundException("Expert doesn't exist!");
    }

    @Transactional(readOnly = true)
    public UserOutDto findByEmail(String email) throws EntityNotFoundException, EmailNotValidException, NullFieldException {
        Validation.isEmailValid(email);
        if (email == null)
            throw new IllegalStateException("Null Email");
        UserExpert byEmail = repository.findByEmail(email);
        if (byEmail == null) throw new EntityNotFoundException("No User Found!");
        return convert2Dto(byEmail);
    }

    @Transactional(readOnly = true)
    public CustomPage<UserOutDto> findByCredit(BigDecimal credit, Pageable pageable) throws EntityNotFoundException {
        Page<UserExpert> page = repository.findByCredit(credit, pageable);
        //if (byCredit.isEmpty()) throw new EntityNotFoundException("No Users Found!");
        return convert2Dto(page);
    }

    @Transactional(readOnly = true)
    public CustomPage<UserOutDto> findByStatus(UserStatus status, Pageable pageable) throws EntityNotFoundException {
        Page<UserExpert> page = repository.findByStatus(status, pageable);
        return convert2Dto(page);
    }

    @Transactional(readOnly = true)
    public CustomPage<UserOutDto> findByExpertise(Long service, Pageable pageable) throws EntityNotFoundException {
        Page<UserExpert> page = repository.findByExpertise(service, pageable);
        return convert2Dto(page);
    }

    @Transactional(readOnly = true)
    boolean finalCheck(UserExpert expert) {
        String email = expert.getEmail();
        UserExpert byEmail = repository.findByEmail(email);
        return byEmail != null && expert.getId() == null;
    }

    @Transactional(readOnly = true)
    public CustomPage<UserSearchOutDto> search(UserSearchInDto user, Pageable pageable) {
        UserSpecification<UserExpert> specification = new UserSpecification<>();
        Specification<UserExpert> filter = specification.getUsers(user);
        Page<UserExpert> all = repository.findAll(filter, pageable);
        CustomPage<UserExpert> result = new CustomPage<>();
        result.setPageSize(all.getSize());
        result.setLastPage(all.getTotalPages());
        result.setPageNumber(all.getNumber());
        result.setTotalElements(all.getTotalElements());
        result.setData(all.getContent());
        return convert2Dto(result);
    }

    private CustomPage<UserSearchOutDto> convert2Dto(CustomPage<UserExpert> list) {
        List<UserSearchOutDto> data = list.getData().stream()
                .map(c -> new UserSearchOutDto().convert2Dto(c)).toList();
        return CustomPage.<UserSearchOutDto>builder()
                .pageSize(list.getPageSize())
                .totalElements(list.getTotalElements())
                .lastPage(list.getLastPage())
                .pageNumber(list.getPageNumber())
                .data(data)
                .build();
    }

    private UserOutDto convert2Dto(UserExpert expert) {
        return UserOutDto.builder()
                .id(expert.getId())
                .name(expert.getName())
                .email(expert.getEmail())
                .credit(expert.getCredit())
                .build();
    }

    private CustomPage<UserOutDto> convert2Dto(Page<UserExpert> page) {
        List<UserOutDto> data = page.getContent().stream().map(this::convert2Dto).toList();
        return CustomPage.<UserOutDto>builder()
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .lastPage(page.getTotalPages())
                .pageNumber(page.getNumber())
                .data(data)
                .build();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public EntityOutDto acceptExpert(Long expertId) throws EntityNotFoundException {
        UserExpert expert = findById(expertId);
        expert.setStatus(ACCEPTED);
        repository.save(expert);
        return new EntityOutDto(expertId);
    }

}
