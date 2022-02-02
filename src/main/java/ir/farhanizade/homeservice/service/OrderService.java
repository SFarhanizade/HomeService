package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.in.ExpertAddSuggestionInDto;
import ir.farhanizade.homeservice.dto.in.RequestInDto;
import ir.farhanizade.homeservice.dto.out.*;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.order.MyOrder;
import ir.farhanizade.homeservice.entity.order.OrderStatus;
import ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus;
import ir.farhanizade.homeservice.entity.order.message.Request;
import ir.farhanizade.homeservice.entity.order.message.Suggestion;
import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.entity.user.UserCustomer;
import ir.farhanizade.homeservice.entity.user.UserExpert;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.repository.order.OrderRepository;
import ir.farhanizade.homeservice.security.user.LoggedInUser;
import ir.farhanizade.homeservice.security.user.UserTypeAndId;
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
import java.util.Set;

import static ir.farhanizade.homeservice.entity.order.OrderStatus.*;
import static ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus.BUSY;
import static ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus.CANCELLED;
import static ir.farhanizade.homeservice.entity.order.message.SuggestionStatus.ACCEPTED;
import static ir.farhanizade.homeservice.entity.order.message.SuggestionStatus.REJECTED;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repository;
    private final SuggestionService suggestionService;
    private final RequestService requestService;
    private final CustomerService customerService;
    private final UserService userService;
    private final ExpertService expertService;

    @Transactional(rollbackFor = Exception.class)
    public EntityOutDto request(RequestInDto request) throws NameNotValidException, NullFieldException, BadEntryException, EmailNotValidException, PasswordNotValidException, EntityNotFoundException, UserNotLoggedInException, AccountIsLockedException {
        Long id = LoggedInUser.id();
        UserCustomer customer = customerService.findById(id);
        return requestService.save(customer, request);
    }

    @Transactional(rollbackFor = Exception.class)
    public ExpertAddSuggestionOutDto suggest(Long orderId, ExpertAddSuggestionInDto request) throws EntityNotFoundException, BusyOrderException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, DuplicateEntityException, UserNotLoggedInException, AccountIsLockedException {
        UserExpert expert = expertService.findById(LoggedInUser.id());
        MyOrder order = findById(orderId);
        Suggestion suggestion = Suggestion.builder()
                .owner(expert)
                .myOrder(order)
                .price(new BigDecimal(request.getPrice()))
                .suggestedDateTime(request.getDateTime())
                .details(request.getDetails())
                .duration(request.getDuration())
                .build();
        ExpertAddSuggestionOutDto result = suggestionService.save(suggestion);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public void save(MyOrder order) {
        repository.save(order);
    }

    @Transactional(readOnly = true)
    public CustomPage<OrderOutDto> loadByExpertises(Set<SubService> expertises, Pageable pageable) throws EntityNotFoundException {
        Page<MyOrder> page = repository.loadByExpertises(expertises, WAITING_FOR_SUGGESTION, WAITING_FOR_SELECTION, CANCELLED, BUSY, pageable);
        return convert2Dto(page);
    }

    @Transactional(readOnly = true)
    public List<MyOrder> loadAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public MyOrder findById(Long id) throws EntityNotFoundException {
        Optional<MyOrder> byId = repository.findById(id);
        if (byId.isPresent()) {
            return byId.get();
        } else throw new EntityNotFoundException("Order Doesn't Exist!");
    }

    @Transactional(readOnly = true)
    public OrderOfUserOutDto getById(Long id) throws EntityNotFoundException {
        return convert2OrderOfUserOutDto(findById(id));
    }

    private OrderOfUserOutDto convert2OrderOfUserOutDto(MyOrder order) {
        Long id = order.getRequest().getOwner().getId();
        Suggestion suggestion = suggestionService.findAcceptedByOrderId(order.getId());
        UserExpert expert = suggestion.getOwner();
        Long expertId = (expert == null) ? null : expert.getId();
        return OrderOfUserOutDto.builder()
                .customerId(id)
                .requestId(order.getRequest().getId())
                .orderId(order.getId())
                .expertId(expertId)
                .suggestionId(suggestion.getId())
                .mainService(order.getService().getParent().getName())
                .subService(order.getService().getName())
                .status(order.getStatus())
                .suggestionStatus(suggestion.getSuggestionStatus())
                .price(suggestion.getPrice())
                .createdTime(order.getCreatedTime())
                .finishTime(order.getFinishDateTime())
                .build();
    }

    @Transactional(readOnly = true)
    public CustomPage<OrderOutDto> loadAvailableOrders(Pageable pageable) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
        UserExpert entity = expertService.findById(LoggedInUser.id());
        Set<SubService> expertises = entity.getExpertises();
        if (expertises.size() == 0) throw new EntityNotFoundException("No Expertises Found For This User!");
        return loadByExpertises(expertises, pageable);
    }

    @Transactional(readOnly = true)
    public OrderOutDto findByIdAndCustomerId(Long orderId) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
        Long id = LoggedInUser.id();
        Optional<MyOrder> byIdAndCustomerId = repository.findByIdAndCustomerId(id, orderId);
        MyOrder result = byIdAndCustomerId.orElseThrow(() -> new EntityNotFoundException("Order Doesn't Exist!"));
        Request request = result.getRequest();
        OrderStatus status = result.getStatus();
        UserExpert expert = new UserExpert();
        Suggestion suggestion = new Suggestion();
        if (!(status.equals(WAITING_FOR_SUGGESTION) || status.equals(WAITING_FOR_SELECTION))) {
            suggestion = suggestionService.findAcceptedByOrderId(orderId);
            expert = suggestion.getOwner();
        }
        return OrderOutDto.builder()
                .id(result.getId())
                .service(result.getService().getName())
                .price(request.getPrice())
                .suggestedDateTime(request.getSuggestedDateTime())
                .createdDateTime(request.getCreatedTime())
                .status(result.getStatus())
                .expertId(expert.getId())
                .expertName(expert.getName())
                .suggestionStatus(suggestion.getSuggestionStatus())
                .build();
    }

    public CustomPage<OrderOfUserOutDto> getOrders(Pageable pageable) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
        UserTypeAndId typeAndId = LoggedInUser.getTypeAndId();
        switch (typeAndId.getRole()) {
            case CUSTOMER: {
                return findOrdersByCustomer(pageable);
            }
            case EXPERT: {
                return getOrdersOfExpert(pageable);
            }
            case ADMIN:{
                return getAllOrders(pageable);
            }
            default: {
                throw new BadEntryException("User Not Allowed");
            }
        }
    }

    private CustomPage<OrderOfUserOutDto> getAllOrders(Pageable pageable){
        Page<MyOrder> page = repository.findAll(pageable);
        CustomPage<OrderOfUserOutDto> result = new CustomPage<>();
        List<MyOrder> content = page.getContent();
        List<OrderOfUserOutDto> data = content.stream()
                .map(this::convert2OrderOfUserOutDto).toList();
        result.setData(data);
        return result.convert(page);
    }

    @Transactional(rollbackFor = Exception.class)
    public EntityOutDto removeOrderByIdAndOwnerId(Long orderId) throws EntityNotFoundException, BadEntryException, UserNotLoggedInException, AccountIsLockedException {
        Long ownerId = LoggedInUser.id();
        MyOrder order = repository.findByIdAndOwnerId(orderId, ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Order Not Found!"));
        Request request = order.getRequest();
        if (!request.getOwner().getId().equals(ownerId)) throw new EntityNotFoundException("This order is not yours!");
        if (request.getStatus().equals(BaseMessageStatus.DONE))
            throw new BadEntryException("You can't cancel this order!");
        request.setStatus(CANCELLED);
        Set<Suggestion> suggestions = order.getSuggestions();
        suggestions.stream()
                .forEach(s -> {
                    s.setSuggestionStatus(REJECTED);
                    s.setStatus(CANCELLED);
                });
        repository.save(order);
        return new EntityOutDto(orderId);
    }

    @Transactional(rollbackFor = Exception.class)
    public EntityOutDto acceptSuggestion(Long id) throws BusyOrderException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, EntityNotFoundException, DuplicateEntityException, UserNotLoggedInException, AccountIsLockedException {
        Long customerId = LoggedInUser.id();
        Suggestion suggestion = suggestionService.findByIdAndCustomerId(id, customerId);
        Validation.isValid(suggestion);
        MyOrder order = suggestion.getMyOrder();
        if (!(order.getStatus().equals(WAITING_FOR_SELECTION) ||
                order.getStatus().equals(WAITING_FOR_SUGGESTION))) throw new BusyOrderException("The order is busy!");
        order.setStatus(WAITING_FOR_EXPERT);
        suggestion.setSuggestionStatus(ACCEPTED);
        Set<Suggestion> suggestions = order.getSuggestions();
        suggestions.stream()
                .filter(s -> !s.equals(suggestion))
                .forEach(s -> s.setSuggestionStatus(REJECTED));
        suggestionService.save(suggestion);
        return new EntityOutDto(id);
    }

    private CustomPage<OrderOutDto> convert2Dto(Page<MyOrder> page) throws EntityNotFoundException {
        if (page.getContent().size() == 0) throw new EntityNotFoundException("No Orders Found!");
        List<OrderOutDto> data = page.getContent().stream().map(o -> convert2Dto(o)).toList();
        return CustomPage.<OrderOutDto>builder()
                .data(data)
                .pageSize(page.getSize())
                .pageNumber(page.getNumber())
                .lastPage(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .build();
    }

    private OrderOutDto convert2Dto(MyOrder o) {
        return OrderOutDto.builder()
                .id(o.getId())
                .status(o.getStatus())
                .service(o.getService().getName())
                .createdDateTime(o.getCreatedTime())
                .price(o.getRequest().getPrice())
                .suggestedDateTime(o.getRequest().getSuggestedDateTime())
                .build();
    }

    public CustomPage<OrderFinishOutDto> findAllByExpertId(Pageable pageable) throws UserNotLoggedInException, BadEntryException, EntityNotFoundException, AccountIsLockedException {
        Long id = LoggedInUser.id();
        Page<MyOrder> page = repository.findAllByExpertId(id, pageable);
        return convert2OutDto(page);
    }

    private CustomPage<OrderFinishOutDto> convert2OutDto(Page<MyOrder> page) {
        List<OrderFinishOutDto> data = page.getContent().stream().map(o -> convert2OutDto(o)).toList();
        CustomPage<OrderFinishOutDto> result = CustomPage.<OrderFinishOutDto>builder()
                .data(data)
                .build();
        return result.convert(page);
    }

    private OrderFinishOutDto convert2OutDto(MyOrder o) {

        Suggestion suggestion = suggestionService.findByStatusAndOrderId(ACCEPTED, o.getId());
        Date startDateTime = null;
        if (suggestion != null) {
            startDateTime = suggestion.getSuggestedDateTime();
        }
        return OrderFinishOutDto.builder()
                .id(o.getId())
                .status(o.getStatus())
                .service(o.getService().getName())
                .price(o.getRequest().getPrice())
                .startDateTime(startDateTime)
                .finishDateTime(o.getFinishDateTime())
                .build();
    }

    public CustomPage<OrderOfUserOutDto> findOrdersByCustomer(Pageable pageable) throws UserNotLoggedInException, BadEntryException, EntityNotFoundException, AccountIsLockedException {
        Long id = LoggedInUser.id();
        Page<MyOrder> page = repository.findAllByCustomerId(id, pageable);
        return convert2CustomPage(page, id);
    }

    public CustomPage<OrderOfUserOutDto> getOrdersOfExpert(Pageable pageable) throws UserNotLoggedInException, BadEntryException, EntityNotFoundException, AccountIsLockedException {
        Long id = LoggedInUser.id();
        Page<MyOrder> page = repository.findAllByExpertId(id, pageable);
        return convert2CustomPage4JustStatus(page, id);
    }

    private CustomPage<OrderOfUserOutDto> convert2CustomPage4JustStatus(Page<MyOrder> page, Long expertId) {
        CustomPage<OrderOfUserOutDto> result = new CustomPage<>();
        List<MyOrder> content = page.getContent();
        List<OrderOfUserOutDto> data = content.stream()
                .map(o -> {
                    Request request = o.getRequest();
                    UserCustomer customer = request.getOwner();
                    Suggestion suggestion = o.getSuggestions().stream()
                            .filter(s -> s.getOwner().getId().equals(expertId)).findFirst().get();
                    return OrderOfUserOutDto.builder()
                            .customerId(customer.getId())
                            .requestId(request.getId())
                            .orderId(o.getId())
                            .expertId(expertId)
                            .suggestionId(suggestion.getId())
                            .mainService(o.getService().getParent().getName())
                            .subService(o.getService().getName())
                            .status(o.getStatus())
                            .suggestionStatus(suggestion.getSuggestionStatus())
                            .createdTime(o.getCreatedTime())
                            .finishTime(o.getFinishDateTime())
                            .build();
                }).toList();

        result.setData(data);
        return result.convert(page);
    }

    public CustomPage<OrderOfUserOutDto> getOrdersByRangeOfTime(Date startTime, Date endTime, Pageable pageable) {
        Page<MyOrder> page = repository.findOrdersByRangeOfTime(startTime, endTime, pageable);
        return convert2CustomPage(page, 0L);
    }

    private CustomPage<OrderOfUserOutDto> convert2CustomPage(Page<MyOrder> page, Long userId) {
        List<MyOrder> orders = page.getContent();
        List<OrderOfUserOutDto> data = orders.stream()
                .map(this::convert2OrderOfUserOutDto).toList();
        CustomPage<OrderOfUserOutDto> result = new CustomPage().convert(page);
        result.setData(data);
        return result;
    }

    public CustomPage<OrderOfUserOutDto> getOrdersByStatus(Integer status, Pageable pageable) {
        Page<MyOrder> page = repository.findByStatus(OrderStatus.values()[status], pageable);
        return convert2CustomPage(page, 0L);
    }

    public CustomPage<OrderOfUserOutDto> getOrdersByMainService(Long id, Pageable pageable) {
        Page<MyOrder> page = repository.findByMainService(id, pageable);
        return convert2CustomPage(page, 0L);
    }

    public CustomPage<OrderOfUserOutDto> getOrdersBySubService(Long id, Pageable pageable) {
        Page<MyOrder> page = repository.findBySubService(id, pageable);
        return convert2CustomPage(page, 0L);
    }

    public RequestAndSuggestionReportOutDto getNumberOfRequestsAndSuggestions() {
        Long requests = requestService.countNumberOfRequests();
        Long suggestions = suggestionService.countNumberOfSuggestions();
        return new RequestAndSuggestionReportOutDto(requests, suggestions);
    }

    public Long getNumberOfDoneOrders() {
        return repository.countDoneOrders();
    }

    public CustomPage<SuggestionOutDto> getSuggestionsByOrder(Long order, Pageable pageable) throws UserNotLoggedInException, BadEntryException, EntityNotFoundException, AccountIsLockedException {
        return suggestionService.findAllByOrderId(order, pageable);
    }
}
