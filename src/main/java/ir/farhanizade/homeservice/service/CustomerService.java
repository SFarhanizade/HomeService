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
import ir.farhanizade.homeservice.service.util.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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

    @Transactional(rollbackFor = Exception.class)
    public EntityOutDto save(UserInDto user) throws UserNotValidException, DuplicateEntityException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException {
        Customer customer = user.convert2Customer();
        boolean isValid = Validation.isValid(customer);

        if (!isValid)
            throw new UserNotValidException("User is not valid!");

        if (finalCheck(customer))
            throw new DuplicateEntityException("User exists!");
        Customer result = repository.save(customer);
        return new EntityOutDto(result.getId());
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

    @Transactional(readOnly = true)
    public CustomPage<UserSearchOutDto> search(UserSearchInDto user, Pageable pageable) throws EntityNotFoundException {
        CustomPage<Customer> search = repository.search(user, pageable);
        if (search.getData().isEmpty()) throw new EntityNotFoundException("Nothing Found!");
        return convert2Dto(search);
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
    public CustomPage<OrderOutDto> getOrders(Long id, Pageable pageable) throws EntityNotFoundException {
        exists(id);
        return orderRepository.findAllByCustomerId(id, pageable);
    }

    @Transactional(readOnly = true)
    public OrderOutDto getOrder(Long id, Long orderId) throws EntityNotFoundException {
        exists(id);
        return orderRepository.findByIdAndCustomerId(id, orderId);
    }

    @Transactional(rollbackFor = Exception.class)
    public EntityOutDto request(Long id, RequestInDto request) throws NameNotValidException, NullFieldException, BadEntryException, EmailNotValidException, PasswordNotValidException, EntityNotFoundException {
        return requestService.save(findById(id), request);
    }

    @Transactional(readOnly = true)
    public boolean exists(Long id) throws EntityNotFoundException {
        boolean exists = repository.existsById(id);
        if (exists)
            return true;
        else
            throw new EntityNotFoundException("User Doesn't Exist!");
    }

    @Transactional(readOnly = true)
    public CustomPage<SuggestionOutDto> getSuggestionsByOrder(Long id, Long order, Pageable pageable) throws EntityNotFoundException {
        exists(id);
        return suggestionService.findAllByOrderId(order, pageable);
    }

    @Transactional(readOnly = true)
    public SuggestionOutDto getSuggestion(Long id, Long suggestion) throws EntityNotFoundException {
        exists(id);
        return suggestionService.getById(suggestion);
    }

    @Transactional(readOnly = true)
    public CustomPage<SuggestionOutDto> getSuggestions(Long id, Pageable pageable) throws EntityNotFoundException {
        exists(id);
        return suggestionService.findAllByCustomerId(id, pageable);
    }

    private UserOutDto convert2Dto(Customer customer) {
        return UserOutDto.builder()
                .id(customer.getId())
                .name(customer.getFName() + " " + customer.getLName())
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
    public EntityOutDto removeOrder(Long id, Long orderId) throws EntityNotFoundException, BadEntryException {
        exists(id);
        return orderRepository.removeOrderByIdAndOwnerId(orderId, id);
    }

    @Transactional(rollbackFor = Exception.class)
    public EntityOutDto acceptSuggestion(Long id, Long suggestion) throws EntityNotFoundException, BusyOrderException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, DuplicateEntityException {
        exists(id);
        return orderRepository.acceptSuggestion(suggestion);
    }

    @Transactional
    public EntityOutDto pay(Long id, Long suggestionId) throws EntityNotFoundException, NotEnoughMoneyException, BusyOrderException, DuplicateEntityException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException {
        Customer customer = findById(id);
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

        EntityOutDto result = transactionService.save(transaction);

        Request request = order.getRequest();
        request.setStatus(BaseMessageStatus.DONE);
        suggestion.setStatus(BaseMessageStatus.DONE);

        suggestionService.save(suggestion);
        return result;
    }


    @Transactional
    public EntityOutDto comment(Long id, Long suggestionId, CommentInDto commentDto) throws EntityNotFoundException {
        Customer customer = findById(id);
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
    public CommentOutDto getComment(Long id, Long commentId) throws EntityNotFoundException {
        exists(id);
        return commentService.findByIdAndCustomerId(id, commentId);
    }
}
