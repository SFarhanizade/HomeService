package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.in.CommentInDto;
import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.out.*;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.MyTransaction;
import ir.farhanizade.homeservice.entity.order.MyComment;
import ir.farhanizade.homeservice.entity.order.MyOrder;
import ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus;
import ir.farhanizade.homeservice.entity.order.message.Request;
import ir.farhanizade.homeservice.entity.order.message.Suggestion;
import ir.farhanizade.homeservice.entity.user.UserCustomer;
import ir.farhanizade.homeservice.entity.user.UserExpert;
import ir.farhanizade.homeservice.entity.user.UserStatus;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.repository.user.CustomerRepository;
import ir.farhanizade.homeservice.security.ApplicationUserRole;
import ir.farhanizade.homeservice.security.user.LoggedInUser;
import ir.farhanizade.homeservice.service.util.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static ir.farhanizade.homeservice.entity.order.OrderStatus.PAID;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository repository;
    private final SuggestionService suggestionService;
    private final TransactionService transactionService;
    private final CommentService commentService;
    private final PasswordEncoder passwordEncoder;


    @Transactional(rollbackFor = Exception.class)
    public Long save(UserInDto user) throws UserNotValidException, DuplicateEntityException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException {
        UserCustomer customer = user.convert2Customer();
        boolean isValid = Validation.isValid(customer);

        if (!isValid)
            throw new UserNotValidException("User is not valid!");

        if (finalCheck(customer))
            throw new DuplicateEntityException("User exists!");
        customer.setRoles(Set.of(ApplicationUserRole.CUSTOMER));
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        Validation.enableUser(customer);
        UserCustomer result = repository.save(customer);
        return result.getId();
    }

    @Transactional(readOnly = true)
    public UserOutDto findByEmail(String email) throws EntityNotFoundException, EmailNotValidException, NullFieldException {
        Validation.isEmailValid(email);
        UserCustomer byEmail = repository.findByEmail(email);
        if (byEmail == null) throw new EntityNotFoundException("User Doesn't Exist!");
        return convert2Dto(byEmail);
    }

    @Transactional(readOnly = true)
    public CustomPage<UserOutDto> findByCredit(BigDecimal credit, Pageable pageable) throws EntityNotFoundException {
        Page<UserCustomer> page = repository.findByCredit(credit, pageable);
        //if (byCredit.isEmpty()) throw new EntityNotFoundException("No Users Found!");
        return convert2Dto(page);
    }

    @Transactional(readOnly = true)
    public CustomPage<UserOutDto> findByStatus(UserStatus status, Pageable pageable) throws EntityNotFoundException {
        Page<UserCustomer> page = repository.findByStatus(status, pageable);
        //if (byStatus.isEmpty()) throw new EntityNotFoundException("No Users Found!");
        return convert2Dto(page);
    }

    @Transactional(readOnly = true)
    public CustomPage<UserOutDto> findAll(Pageable pageable) throws EntityNotFoundException {
        Page<UserCustomer> page = repository.findAll(pageable);
        if (page.getContent().isEmpty()) throw new EntityNotFoundException("No Users Found!");
        return convert2Dto(page);
    }

    @Transactional(readOnly = true)
    boolean finalCheck(UserCustomer customer) {
        String email = customer.getEmail();
        UserCustomer byEmail = repository.findByEmail(email);
        return byEmail != null && customer.getId() == null;
    }

    private CustomPage<UserSearchOutDto> convert2Dto(CustomPage<UserCustomer> list) {
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

    @Transactional(readOnly = true)
    public UserCustomer findById(Long id) throws EntityNotFoundException {
        Optional<UserCustomer> byId = repository.findById(id);
        if (byId.isPresent()) {
            return byId.get();
        } else
            throw new EntityNotFoundException("User doesn't exist!");
    }

    @Deprecated(forRemoval = true)
    @Transactional(readOnly = true)
    public boolean exists(Long id) throws EntityNotFoundException {
        boolean exists = repository.existsById(id);
        if (exists)
            return true;
        else
            throw new EntityNotFoundException("User Doesn't Exist!");
    }

    private UserOutDto convert2Dto(UserCustomer customer) {
        return UserOutDto.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .credit(customer.getCredit())
                .build();
    }

    private CustomPage<UserOutDto> convert2Dto(Page<UserCustomer> page) {
        List<UserOutDto> data = page.getContent().stream().map(this::convert2Dto).toList();
        return CustomPage.<UserOutDto>builder()
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .lastPage(page.getTotalPages())
                .pageNumber(page.getNumber())
                .data(data)
                .build();
    }

    @Transactional
    public EntityOutDto pay(Long suggestionId, String method) throws EntityNotFoundException, NotEnoughMoneyException, BusyOrderException, DuplicateEntityException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, UserNotLoggedInException, AccountIsLockedException {
        UserCustomer customer = findById(LoggedInUser.id());
        Suggestion suggestion = suggestionService.findById(suggestionId);
        if (suggestion.getMyOrder().getStatus().equals(PAID))
            throw new BadEntryException("You can't pay more than once!");
        UserExpert expert = suggestion.getOwner();
        BigDecimal price = suggestion.getPrice();

        MyOrder order = suggestion.getMyOrder();
        order.setStatus(PAID);

        MyTransaction transaction = MyTransaction.builder()
                .payer(customer)
                .recipient(expert)
                .amount(price)
                .myOrder(order)
                .build();

        EntityOutDto result = transactionService.save(transaction, method);

        Request request = order.getRequest();
        request.setStatus(BaseMessageStatus.DONE);
        suggestion.setStatus(BaseMessageStatus.DONE);

        suggestionService.save(suggestion);
        return result;
    }


    @Transactional
    public EntityOutDto comment(Long suggestionId, CommentInDto commentDto) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
        UserCustomer customer = findById(LoggedInUser.id());
        Suggestion suggestion = suggestionService.findById(suggestionId);
        UserExpert expert = suggestion.getOwner();
        MyOrder order = suggestion.getMyOrder();
        MyComment comment = MyComment.builder()
                .myCustomer(customer)
                .myExpert(expert)
                .myOrder(order)
                .points(commentDto.getPoints())
                .description(commentDto.getDescription())
                .build();
        return commentService.save(comment);
    }
}
