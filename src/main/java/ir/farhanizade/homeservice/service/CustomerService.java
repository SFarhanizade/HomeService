package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.in.RequestInDto;
import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.in.UserSearchInDto;
import ir.farhanizade.homeservice.dto.out.*;
import ir.farhanizade.homeservice.entity.order.Order;
import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.entity.user.UserStatus;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.repository.user.CustomerRepository;
import ir.farhanizade.homeservice.service.util.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository repository;
    private final OrderService orderRepository;
    private final RequestService requestService;
    private final SuggestionService suggestionService;

    @Transactional
    public EntityOutDto save(UserInDto user) throws UserNotValidException, DuplicateEntityException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException {
        Customer customer = user.convert2Customer();
        boolean isValid = false;
        isValid = Validation.isValid(customer);

        if (!isValid)
            throw new UserNotValidException("User is not valid!");

        if (finalCheck(customer))
            throw new DuplicateEntityException("User exists!");
        Customer result = repository.save(customer);
        return new EntityOutDto(result.getId());
    }

    @Transactional(readOnly = true)
    public UserOutDto findByEmail(String email) throws EntityNotFoundException {
        if (email == null)
            throw new IllegalStateException("Null Email");
        Customer byEmail = repository.findByEmail(email);
        if (byEmail == null) throw new EntityNotFoundException("User Doesn't Exist!");
        return convert2Dto(byEmail);
    }

    @Transactional(readOnly = true)
    public List<UserOutDto> findByCredit(BigDecimal credit) throws EntityNotFoundException {
        List<Customer> byCredit = repository.findByCredit(credit);
        if (byCredit.isEmpty()) throw new EntityNotFoundException("No Users Found!");
        return convert2Dto(byCredit);
    }

    @Transactional(readOnly = true)
    public List<UserOutDto> findByStatus(UserStatus status) throws EntityNotFoundException {
        List<Customer> byStatus = repository.findByStatus(status);
        if (byStatus.isEmpty()) throw new EntityNotFoundException("No Users Found!");
        return convert2Dto(byStatus);
    }

    @Transactional(readOnly = true)
    public List<UserOutDto> findAll() throws EntityNotFoundException {
        List<Customer> all = repository.findAll();
        if (all.isEmpty()) throw new EntityNotFoundException("No Users Found!");
        return convert2Dto(all);
    }

    @Transactional(readOnly = true)
    boolean finalCheck(Customer customer) {
        String email = customer.getEmail();
        Customer byEmail = repository.findByEmail(email);
        return byEmail != null && customer.getId() == null;
    }

    @Transactional(readOnly = true)
    public List<UserSearchOutDto> search(UserSearchInDto user) {
        List<Customer> searchResult = repository.search(user);
        List<UserSearchOutDto> result = searchResult.stream()
                .map(e -> new UserSearchOutDto().convert2Dto(e))
                .peek(e -> e.setType("customer"))
                .toList();
        return result;
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
    public List<OrderOutDto> getOrders(Long id) throws EntityNotFoundException {
        exists(id);
        List<Order> orders = orderRepository.findAllByCustomerId(id);
        List<OrderOutDto> result = orders.stream()
                .map((Order o) -> OrderOutDto.builder()
                        .id(o.getId())
                        .service(o.getService().getName())
                        .price(o.getRequest().getPrice())
                        .suggestedDateTime(o.getRequest().getSuggestedDateTime())
                        .createdDateTime(o.getRequest().getDateTime())
                        .status(o.getStatus())
                        .build()).toList();
        return result;
    }

    @Transactional(readOnly = true)
    public OrderOutDto getOrder(Long id, Long orderId) throws EntityNotFoundException {
        exists(id);
        return orderRepository.findByIdAndCustomerId(id, orderId);
    }

    @Transactional
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
    public List<SuggestionOutDto> getSuggestionsByOrder(Long id, Long order) throws EntityNotFoundException {
        exists(id);
        return suggestionService.findAllByOrderId(order);
    }

    @Transactional(readOnly = true)
    public SuggestionOutDto getSuggestion(Long id, Long suggestion) throws EntityNotFoundException {
        exists(id);
        return suggestionService.findById(suggestion);
    }

    @Transactional(readOnly = true)
    public List<SuggestionOutDto> getSuggestions(Long id) throws EntityNotFoundException {
        exists(id);
        return suggestionService.findAllByCustomerId(id);
    }

    private UserOutDto convert2Dto(Customer customer) {
        return UserOutDto.builder()
                .id(customer.getId())
                .name(customer.getFName() + " " + customer.getLName())
                .email(customer.getEmail())
                .credit(customer.getCredit())
                .build();
    }

    private List<UserOutDto> convert2Dto(List<Customer> customers) {
        return customers.stream()
                .map(this::convert2Dto).toList();
    }

    @Transactional
    public EntityOutDto removeOrder(Long id, Long orderId) throws EntityNotFoundException {
        exists(id);
        orderRepository.removeOrderByIdAndOwnerId(id, orderId);
        return new EntityOutDto(orderId);
    }

    @Transactional
    public EntityOutDto acceptSuggestion(Long id, Long suggestion) throws EntityNotFoundException, BusyOrderException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException {
        exists(id);
        return orderRepository.acceptSuggestion(suggestion);
    }
}
