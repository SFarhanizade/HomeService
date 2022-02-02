package ir.farhanizade.homeservice.service;


import ir.farhanizade.homeservice.controller.api.filter.UserSpecification;
import ir.farhanizade.homeservice.dto.in.*;
import ir.farhanizade.homeservice.dto.out.*;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.user.UserCustomer;
import ir.farhanizade.homeservice.entity.user.UserExpert;
import ir.farhanizade.homeservice.entity.user.MyUser;
import ir.farhanizade.homeservice.entity.user.UserStatus;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.repository.user.UserRepository;
import ir.farhanizade.homeservice.security.ApplicationUserRole;
import ir.farhanizade.homeservice.security.user.LoggedInUser;
import ir.farhanizade.homeservice.security.user.UserUUID;
import ir.farhanizade.homeservice.service.util.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final ExpertService expertService;
    private final CustomerService customerService;
    private final TransactionService transactionService;
    private final CommentService commentService;
    private final PasswordEncoder passwordEncoder;


    @Transactional(rollbackFor = Exception.class)
    public EntityOutDto changePassword(UserPasswordInDto user) throws PasswordNotValidException, WrongPasswordException, EntityNotFoundException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
        if (!Validation.passwordIsValid(user.getNewPassword())) {
            throw new PasswordNotValidException("The new password is not valid!");
        }
        if (user.getCurrentPassword().equals(user.getNewPassword())) {
            throw new PasswordNotValidException("The new password is the same as the current password!");
        }
        String currentPassword = user.getCurrentPassword();
        Long id = LoggedInUser.id();
        MyUser entity = findById(id);

        if (!passwordEncoder.matches(currentPassword, entity.getPassword())) {
            throw new WrongPasswordException("The current password is not correct!");
        }
        String newPassword = passwordEncoder.encode(user.getNewPassword());
        entity.setPassword(newPassword);
        repository.save(entity);
        return new EntityOutDto(id);
    }

    public UUIDOutDto save(UserInDto user) throws DuplicateEntityException, NameNotValidException, EmailNotValidException, PasswordNotValidException, UserNotValidException, NullFieldException, UnsupportedEncodingException, NoSuchAlgorithmException {
        Long id;
        if ("expert".equals(user.getType())) {
            id = expertService.save(user);
        } else if ("customer".equals(user.getType())) {
            id = customerService.save(user);
        } else {
            throw new UserNotValidException("User is not valid!");
        }
        String uuid = UserUUID.createUUID(id);
        return new UUIDOutDto(uuid);
    }

    @Transactional
    public EntityOutDto verifyEmail(String uuid) throws UserNotLoggedInException, BadEntryException, EntityNotFoundException, UUIDNotFoundException {
        Long id = UserUUID.getIdByUUID(uuid);
        MyUser user = findById(id);
        ApplicationUserRole role = getRole(user);
        UserStatus status = null;
        switch (role) {
            case CUSTOMER -> status = UserStatus.ACCEPTED;
            case EXPERT -> status = UserStatus.PENDING;
        }
        user.setStatus(status);
        repository.save(user);
        return new EntityOutDto(id);
    }

    private ApplicationUserRole getRole(MyUser user) throws BadEntryException {
        String roleStr = user.getAuthorities().stream()
                .filter(a -> a.getAuthority().startsWith("ROLE_"))
                .findFirst()
                .orElseThrow(() -> new BadEntryException("User Not Allowed!"))
                .getAuthority()
                .substring(5);
        return ApplicationUserRole.valueOf(roleStr);
    }

    @Transactional(readOnly = true)
    public CustomPage<UserSearchOutDto> search(UserSearchInDto user, Pageable pageable) throws EntityNotFoundException {
        if (isExpert(user)) {
            return expertService.search(user, pageable);
        }
        UserSpecification<MyUser> specification = new UserSpecification<>();
        Specification<MyUser> filter = specification.getUsers(user);
        Page<MyUser> all = repository.findAll(filter, pageable);
        CustomPage<MyUser> result = new CustomPage<>();
        result.setPageSize(all.getSize());
        result.setLastPage(all.getTotalPages());
        result.setPageNumber(all.getNumber());
        result.setTotalElements(all.getTotalElements());
        result.setData(all.getContent());
        return convert2Dto(result);
    }

    private boolean isExpert(UserSearchInDto user) {
        boolean hasExpertises = user.getExpertises() != null;
        boolean isExpert = "expert".equals(user.getType());
        boolean hasPoints = user.getPoints() != null;
        return hasPoints || hasExpertises || isExpert;
    }

    @Transactional(readOnly = true)
    public CustomPage<TransactionOutDto> getTransactions(Pageable pageable) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
        return transactionService.findByUserId(pageable);
    }

    private CustomPage<UserSearchOutDto> convert2Dto(CustomPage<MyUser> list) {
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

    /**
     * Use LoggedInUser class instead
     */
    @Deprecated(forRemoval = true)
    public boolean exists(Long id) {
        return repository.existsById(id);
    }

    @Transactional(readOnly = true)
    public TransactionOutDto getTransaction(Long transaction) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
        return transactionService.findById(transaction);
    }

    @Transactional(readOnly = true)
    public CustomPage<CommentOutDto> getComments(Pageable pageable) throws UserNotLoggedInException, BadEntryException, EntityNotFoundException, AccountIsLockedException {
        return commentService.findAllByUserId(pageable);
    }

    @Transactional(readOnly = true)
    public UserCreditOutDto loadCredit() throws EntityNotFoundException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
        MyUser user = findById(LoggedInUser.id());
        return new UserCreditOutDto(user.getId(), user.getCredit());
    }

    @Transactional
    public UserIncreaseCreditOutDto increaseCredit(UserIncreaseCreditInDto request) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
        MyUser user = findById(LoggedInUser.id());
        user.setCredit(user.getCredit().add(new BigDecimal(request.getAmount())));
        MyUser saved = repository.save(user);
        return UserIncreaseCreditOutDto.builder()
                .id(saved.getId())
                .amount(request.getAmount())
                .balance(saved.getCredit())
                .build();
    }

    public MyUser findById(Long id) throws EntityNotFoundException {
        Optional<MyUser> byId = repository.findById(id);
        return byId.orElseThrow(() -> new EntityNotFoundException("User not found!"));
    }

    public ReportRegisterTimeUsersOutDto getNumberOfUsersByRegisterTime(TimeRangeInDto timeRange) {
        Long customer = repository.getNumberOfCustomersByRegisterTime(timeRange.getStartTime(), timeRange.getEndTime());
        Long expert = repository.getNumberOfExpertsByRegisterTime(timeRange.getStartTime(), timeRange.getEndTime());
        return new ReportRegisterTimeUsersOutDto(expert, customer);
    }

    public Long convertUsername2Id(String username) throws EntityNotFoundException {
        return repository.getIdByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User Not Found!"));
    }

    public UserStatus getStatusById(Long id) throws EntityNotFoundException {
        return repository.getStatusById(id)
                .orElseThrow(() -> new EntityNotFoundException("User Not Found!"));
    }

    public UserStatus getStatusByUsername(String username) throws EntityNotFoundException {
        return repository.getStatusByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User Not Found!"));
    }

    public UserSearchOutDto getUserById(Long id) throws EntityNotFoundException {
        Optional<MyUser> byId = repository.findById(id);
        MyUser user = byId.orElseThrow(() -> new EntityNotFoundException("User Not Found!"));
        UserSearchOutDto result = new UserSearchOutDto().convert2Dto(user);
        if (user instanceof UserExpert) {
            result.setType("expert");
        } else if (user instanceof UserCustomer) {
            result.setType("customer");
        }
        return result;
    }
}
