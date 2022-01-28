package ir.farhanizade.homeservice.service;


import ir.farhanizade.homeservice.controller.api.filter.UserSpecification;
import ir.farhanizade.homeservice.dto.in.*;
import ir.farhanizade.homeservice.dto.out.*;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.entity.user.User;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.repository.user.UserRepository;
import ir.farhanizade.homeservice.security.user.LoggedInUser;
import ir.farhanizade.homeservice.security.user.UserTypeAndId;
import ir.farhanizade.homeservice.service.util.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private final OrderService orderService;
    private final PasswordEncoder passwordEncoder;


    @Transactional(rollbackFor = Exception.class)
    public EntityOutDto changePassword(UserPasswordInDto user) throws PasswordNotValidException, WrongPasswordException, EntityNotFoundException, UserNotLoggedInException, BadEntryException {
        String currentPassword = passwordEncoder.encode(user.getCurrentPassword());
        String newPassword = passwordEncoder.encode(user.getNewPassword());
        Long id = LoggedInUser.id();
        User entity = findById(id);
        if (currentPassword.equals(newPassword)) {
            throw new PasswordNotValidException("The new password is the same as the current password!");
        }
        if (!Validation.passwordIsValid(newPassword)) {
            throw new PasswordNotValidException("The new password is not valid!");
        }

        if (!currentPassword.equals(entity.getPassword())) {
            throw new WrongPasswordException("The current password is not correct!");
        }
        entity.setPassword(newPassword);
        repository.save(entity);
        return new EntityOutDto(id);
    }

    public EntityOutDto save(UserInDto user, Class<?> type) throws DuplicateEntityException, NameNotValidException, EmailNotValidException, PasswordNotValidException, UserNotValidException, NullFieldException {
        EntityOutDto result;
        if (type == Expert.class) {
            result = expertService.save(user);

        } else /*if (type == Customer.class)*/ {
            result = customerService.save(user);
        } /*else {
            result = adminService.save(user);
        }*/
        return result;
    }

    @Transactional(readOnly = true)//TODO: should move to AdminService
    public CustomPage<UserSearchOutDto> search(UserSearchInDto user, Pageable pageable) throws EntityNotFoundException {
        if (isExpert(user)) {
            return expertService.search(user, pageable);
        }
        UserSpecification<User> specification = new UserSpecification<>();
        Specification<User> filter = specification.getUsers(user);
        Page<User> all = repository.findAll(filter, pageable);
        CustomPage<User> result = new CustomPage<>();
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
    public CustomPage<TransactionOutDto> getTransactions(Pageable pageable) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException {
        return transactionService.findByUserId(pageable);
    }

    private CustomPage<UserSearchOutDto> convert2Dto(CustomPage<User> list) {
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
    public TransactionOutDto getTransaction(Long transaction) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException {
        return transactionService.findById(transaction);
    }

    @Transactional(readOnly = true)
    public CustomPage<CommentOutDto> getComments(Pageable pageable) throws UserNotLoggedInException, BadEntryException, EntityNotFoundException {
        return commentService.findAllByUserId(pageable);
    }

    @Transactional(readOnly = true)
    public UserCreditOutDto loadCredit() throws EntityNotFoundException, UserNotLoggedInException, BadEntryException {
        User user = findById(LoggedInUser.id());
        return new UserCreditOutDto(user.getId(), user.getCredit());
    }

    @Transactional
    public UserIncreaseCreditOutDto increaseCredit(UserIncreaseCreditInDto request) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException {
        User user = findById(LoggedInUser.id());
        user.setCredit(user.getCredit().add(new BigDecimal(request.getAmount())));
        User saved = repository.save(user);
        return UserIncreaseCreditOutDto.builder()
                .id(saved.getId())
                .amount(request.getAmount())
                .balance(saved.getCredit())
                .build();
    }

    public User findById(Long id) throws EntityNotFoundException {
        Optional<User> byId = repository.findById(id);
        return byId.orElseThrow(() -> new EntityNotFoundException("User not found!"));
    }

    public CustomPage<OrderOfUserOutDto> getOrders(Pageable pageable) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException {
        UserTypeAndId typeAndId = LoggedInUser.getTypeAndId();
        switch (typeAndId.getRole()) {
            case CUSTOMER: {
                return orderService.findOrdersByCustomer(pageable);
            }
            case EXPERT: {
                return orderService.getOrdersOfExpert(pageable);
            }
            default: {
                throw new BadEntryException("User Not Allowed");
            }
        }
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
}
