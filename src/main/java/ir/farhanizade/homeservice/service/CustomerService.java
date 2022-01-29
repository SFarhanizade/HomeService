package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.in.CommentInDto;
import ir.farhanizade.homeservice.dto.in.RequestInDto;
import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.in.UserSearchInDto;
import ir.farhanizade.homeservice.dto.out.*;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.Transaction;
import ir.farhanizade.homeservice.entity.order.Comment;
import ir.farhanizade.homeservice.entity.order.Order;
import ir.farhanizade.homeservice.entity.order.OrderStatus;
import ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus;
import ir.farhanizade.homeservice.entity.order.message.Request;
import ir.farhanizade.homeservice.entity.order.message.Suggestion;
import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.entity.user.UserStatus;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.repository.user.CustomerRepository;
import ir.farhanizade.homeservice.security.ApplicationUserRole;
import ir.farhanizade.homeservice.security.user.LoggedInUser;
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
import java.util.Set;

import static ir.farhanizade.homeservice.entity.order.OrderStatus.PAID;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository repository;
    private final OrderService orderRepository;
    private final RequestService requestService;
    private final SuggestionService suggestionService;
    private final TransactionService transactionService;
    private final CommentService commentService;
    private final PasswordEncoder passwordEncoder;


    @Transactional(rollbackFor = Exception.class)
    public Long save(UserInDto user) throws UserNotValidException, DuplicateEntityException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException {
        Customer customer = user.convert2Customer();
        boolean isValid = Validation.isValid(customer);

        if (!isValid)
            throw new UserNotValidException("User is not valid!");

        if (finalCheck(customer))
            throw new DuplicateEntityException("User exists!");
        customer.setRoles(Set.of(ApplicationUserRole.CUSTOMER));
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        Validation.enableUser(customer);
        Customer result = repository.save(customer);
        return result.getId();
    }

    @Transactional(readOnly = true)
    public UserOutDto findByEmail(String email) throws EntityNotFoundException, EmailNotValidException, NullFieldException {
        Validation.isEmailValid(email);
        Customer byEmail = repository.findByEmail(email);
        if (byEmail == null) throw new EntityNotFoundException("User Doesn't Exist!");
        return convert2Dto(byEmail);
    }

    @Transactional(readOnly = true)
    public CustomPage<UserOutDto> findByCredit(BigDecimal credit, Pageable pageable) throws EntityNotFoundException {
        Page<Customer> page = repository.findByCredit(credit, pageable);
        //if (byCredit.isEmpty()) throw new EntityNotFoundException("No Users Found!");
        return convert2Dto(page);
    }

    @Transactional(readOnly = true)
    public CustomPage<UserOutDto> findByStatus(UserStatus status, Pageable pageable) throws EntityNotFoundException {
        Page<Customer> page = repository.findByStatus(status, pageable);
        //if (byStatus.isEmpty()) throw new EntityNotFoundException("No Users Found!");
        return convert2Dto(page);
    }

    @Transactional(readOnly = true)
    public CustomPage<UserOutDto> findAll(Pageable pageable) throws EntityNotFoundException {
        Page<Customer> page = repository.findAll(pageable);
        if (page.getContent().isEmpty()) throw new EntityNotFoundException("No Users Found!");
        return convert2Dto(page);
    }

    @Transactional(readOnly = true)
    boolean finalCheck(Customer customer) {
        String email = customer.getEmail();
        Customer byEmail = repository.findByEmail(email);
        return byEmail != null && customer.getId() == null;
    }

    private CustomPage<UserSearchOutDto> convert2Dto(CustomPage<Customer> list) {
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
    public Customer findById(Long id) throws EntityNotFoundException {
        Optional<Customer> byId = repository.findById(id);
        if (byId.isPresent()) {
            return byId.get();
        } else
            throw new EntityNotFoundException("User doesn't exist!");
    }

    @Transactional(readOnly = true)
    public CustomPage<OrderOutDto> getOrders(Pageable pageable) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException {
        return orderRepository.findAllByCustomerId(pageable);
    }

    @Transactional(readOnly = true)
    public OrderOutDto getOrder(Long orderId) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException {
        return orderRepository.findByIdAndCustomerId(orderId);
    }

    @Transactional(rollbackFor = Exception.class)
    public EntityOutDto request(RequestInDto request) throws NameNotValidException, NullFieldException, BadEntryException, EmailNotValidException, PasswordNotValidException, EntityNotFoundException, UserNotLoggedInException {
        Long id = LoggedInUser.id();
        return requestService.save(findById(id), request);
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

    @Transactional(readOnly = true)
    public CustomPage<SuggestionOutDto> getSuggestionsByOrder(Long order, Pageable pageable) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException {
        return suggestionService.findAllByOrderId(order, pageable);
    }

    @Transactional(readOnly = true)
    public SuggestionOutDto getSuggestion(Long suggestion) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException {
        return suggestionService.getById(suggestion);
    }

    @Transactional(readOnly = true)
    public CustomPage<SuggestionOutDto> getSuggestions(Pageable pageable) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException {
        return suggestionService.findAllByCustomerId(pageable);
    }

    private UserOutDto convert2Dto(Customer customer) {
        return UserOutDto.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .credit(customer.getCredit())
                .build();
    }

    private CustomPage<UserOutDto> convert2Dto(Page<Customer> page) {
        List<UserOutDto> data = page.getContent().stream().map(this::convert2Dto).toList();
        return CustomPage.<UserOutDto>builder()
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .lastPage(page.getTotalPages())
                .pageNumber(page.getNumber())
                .data(data)
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public EntityOutDto removeOrder(Long orderId) throws EntityNotFoundException, BadEntryException, UserNotLoggedInException {
        return orderRepository.removeOrderByIdAndOwnerId(orderId);
    }

    @Transactional(rollbackFor = Exception.class)
    public EntityOutDto acceptSuggestion(Long suggestion) throws EntityNotFoundException, BusyOrderException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, DuplicateEntityException, UserNotLoggedInException {
        return orderRepository.acceptSuggestion(suggestion);
    }

    @Transactional
    public EntityOutDto pay(Long suggestionId, String method) throws EntityNotFoundException, NotEnoughMoneyException, BusyOrderException, DuplicateEntityException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, UserNotLoggedInException {
        Customer customer = findById(LoggedInUser.id());
        Suggestion suggestion = suggestionService.findById(suggestionId);
        if (suggestion.getOrder().getStatus().equals(PAID))
            throw new BadEntryException("You can't pay more than once!");
        Expert expert = suggestion.getOwner();
        BigDecimal price = suggestion.getPrice();

        Order order = suggestion.getOrder();
        order.setStatus(PAID);

        Transaction transaction = Transaction.builder()
                .payer(customer)
                .recipient(expert)
                .amount(price)
                .order(order)
                .build();

        EntityOutDto result = transactionService.save(transaction, method);

        Request request = order.getRequest();
        request.setStatus(BaseMessageStatus.DONE);
        suggestion.setStatus(BaseMessageStatus.DONE);

        suggestionService.save(suggestion);
        return result;
    }


    @Transactional
    public EntityOutDto comment(Long suggestionId, CommentInDto commentDto) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException {
        Customer customer = findById(LoggedInUser.id());
        Suggestion suggestion = suggestionService.findById(suggestionId);
        Expert expert = suggestion.getOwner();
        Order order = suggestion.getOrder();
        Comment comment = Comment.builder()
                .customer(customer)
                .expert(expert)
                .order(order)
                .points(commentDto.getPoints())
                .description(commentDto.getDescription())
                .build();
        return commentService.save(comment);
    }

    @Transactional(readOnly = true)
    public CommentOutDto getComment(Long commentId) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException {
        return commentService.findByIdAndCustomerId(commentId);
    }
}
