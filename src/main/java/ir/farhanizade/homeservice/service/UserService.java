package ir.farhanizade.homeservice.service;


import ir.farhanizade.homeservice.dto.in.*;
import ir.farhanizade.homeservice.dto.out.*;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.order.Order;
import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.entity.user.User;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.repository.user.UserRepository;
import ir.farhanizade.homeservice.service.util.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final ExpertService expertService;
    private final CustomerService customerService;
    private final AdminService adminService;
    private final TransactionService transactionService;
    private final CommentService commentService;
    private final OrderService orderService;

    @Transactional(rollbackFor = Exception.class)
    public EntityOutDto changePassword(UserPasswordInDto user) throws PasswordNotValidException, WrongPasswordException, EntityNotFoundException {
        String currentPassword = user.getCurrentPassword();
        String newPassword = user.getNewPassword();
        Long id = user.getId();
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

        } else if (type == Customer.class) {
            result = customerService.save(user);
        } else {
            result = adminService.save(user);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public CustomPage<UserSearchOutDto> search(UserSearchInDto user, Pageable pageable) throws EntityNotFoundException {
        CustomPage<UserSearchOutDto> result;
        if ("expert".equals(user.getType())) {
            result = expertService.search(user, pageable);
        } else if ("customer".equals(user.getType())) {
            result = customerService.search(user, pageable);
        } else {
            result = searchUser(user, pageable);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public CustomPage<TransactionOutDto> getTransactions(Long id, Pageable pageable) throws EntityNotFoundException {
        exists(id);
        return transactionService.findByUserId(id, pageable);
    }

    @Transactional(readOnly = true)
    CustomPage<UserSearchOutDto> searchUser(UserSearchInDto user, Pageable pageable) {
        CustomPage<User> searchResult = repository.search(user, pageable);
        return convert2Dto(searchResult);
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

    public boolean exists(Long id) {
        return repository.existsById(id);
    }

    public TransactionOutDto getTransaction(Long id, Long transaction) throws EntityNotFoundException {
        exists(id);
        return transactionService.findById(transaction);
    }

    @Transactional(readOnly = true)
    public CustomPage<CommentOutDto> getComments(Long id, Pageable pageable) {
        exists(id);
        return commentService.findAllByUserId(id, pageable);
    }

    @Transactional(readOnly = true)
    public UserCreditOutDto loadCreditById(Long id) throws EntityNotFoundException {
        User user = findById(id);
        return new UserCreditOutDto(user.getId(), user.getCredit());
    }

    @Transactional
    public UserIncreaseCreditOutDto increaseCredit(Long id, UserIncreaseCreditInDto request) throws EntityNotFoundException {
        User user = findById(id);
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

    public CustomPage<OrderOfUserOutDto> getOrders(Long id, Pageable pageable) throws EntityNotFoundException {
        if (customerService.exists(id))
            return orderService.findOrdersByCustomer(id, pageable);
        else
            return orderService.getOrdersOfExpert(id, pageable);
    }

    public ReportRegisterTimeUsersOutDto getNumberOfUsersByRegisterTime(TimeRangeInDto timeRange, Pageable pageable) {
        Long customer = repository.getNumberOfCustomersByRegisterTime(timeRange.getTime1(), timeRange.getTime2(), pageable);
        Long expert = repository.getNumberOfExpertsByRegisterTime(timeRange.getTime1(), timeRange.getTime2(), pageable);
        return new ReportRegisterTimeUsersOutDto(expert, customer);
    }
}
