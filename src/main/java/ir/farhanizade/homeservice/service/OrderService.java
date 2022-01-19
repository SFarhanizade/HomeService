package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.dto.out.OrderFinishOutDto;
import ir.farhanizade.homeservice.dto.out.OrderOfUserOutDto;
import ir.farhanizade.homeservice.dto.out.OrderOutDto;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.order.Order;
import ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus;
import ir.farhanizade.homeservice.entity.order.message.Request;
import ir.farhanizade.homeservice.entity.order.message.Suggestion;
import ir.farhanizade.homeservice.entity.order.message.SuggestionStatus;
import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.repository.order.OrderRepository;
import ir.farhanizade.homeservice.service.util.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

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

    @Transactional(rollbackFor = Exception.class)
    public void save(Order order) {
        repository.save(order);
    }

    @Transactional(readOnly = true)
    public CustomPage<OrderOutDto> loadByExpertises(Set<SubService> expertises, Pageable pageable) throws EntityNotFoundException {
        Page<Order> page = repository.loadByExpertises(expertises, WAITING_FOR_SUGGESTION, WAITING_FOR_SELECTION, CANCELLED, BUSY, pageable);
        return convert2Dto(page);
//        if (orders.size() == 0) {
//            throw new EntityNotFoundException("No Orders Found!");
//        }
//        return orders;
    }

    @Transactional(readOnly = true)
    public List<Order> loadAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Order findById(Long id) throws EntityNotFoundException {
        Optional<Order> byId = repository.findById(id);
        if (byId.isPresent()) {
            return byId.get();
        } else throw new EntityNotFoundException("Order Doesn't Exist!");
    }

    @Transactional(readOnly = true)
    public OrderOutDto findByIdAndCustomerId(Long id, Long orderId) throws EntityNotFoundException {
        Optional<Order> byIdAndCustomerId = repository.findByIdAndCustomerId(id, orderId);
        if (byIdAndCustomerId.isPresent()) {
            Order result = byIdAndCustomerId.get();
            return OrderOutDto.builder()
                    .id(result.getId())
                    .service(result.getService().getName())
                    .price(result.getRequest().getPrice())
                    .suggestedDateTime(result.getRequest().getSuggestedDateTime())
                    .createdDateTime(result.getRequest().getCreatedTime())
                    .status(result.getStatus())
                    .build();
        }
        throw new EntityNotFoundException("Order Doesn't Exist!");
    }

    @Transactional(readOnly = true)
    public CustomPage<OrderOutDto> findAllByCustomerId(Long ownerId, Pageable pageable) {
        Page<Order> page = repository.findAllByCustomerId(ownerId, pageable);
        return convert2Dto(page);
    }

    @Transactional(rollbackFor = Exception.class)
    public EntityOutDto removeOrderByIdAndOwnerId(Long orderId, Long ownerId) throws EntityNotFoundException, BadEntryException {
        Order order = findById(orderId);
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
    public EntityOutDto acceptSuggestion(Long id) throws BusyOrderException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, EntityNotFoundException, DuplicateEntityException {
        Suggestion suggestion = suggestionService.findById(id);
        Validation.isValid(suggestion);
        Order order = suggestion.getOrder();
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

//    public CustomPage<OrderOutDto> findByExpertise(Set<SubService> expertises, Pageable pageable){
//        CustomPage<Order> byExpertises = repository.findByExpertises(expertises, pageable);
//        List<Order> data = byExpertises.getData();
//        List<OrderOutDto> orders = convert2Dto(data);
//        return CustomPage.<OrderOutDto>builder()
//                .data(orders)
//                .pageSize(byExpertises.getPageSize())
//                .lastPage(byExpertises.getLastPage())
//                .pageNumber(byExpertises.getPageNumber())
//                .build();
//    }

    private CustomPage<OrderOutDto> convert2Dto(Page<Order> page) {
        List<OrderOutDto> data = page.getContent().stream().map(o -> convert2Dto(o)).toList();
        return CustomPage.<OrderOutDto>builder()
                .data(data)
                .pageSize(page.getSize())
                .pageNumber(page.getNumber())
                .lastPage(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .build();
    }

    private OrderOutDto convert2Dto(Order o) {
        return OrderOutDto.builder()
                .id(o.getId())
                .status(o.getStatus())
                .service(o.getService().getName())
                .createdDateTime(o.getCreatedTime())
                .price(o.getRequest().getPrice())
                .suggestedDateTime(o.getRequest().getSuggestedDateTime())
                .build();
    }

    public CustomPage<OrderFinishOutDto> findAllByExpertId(Long id, Pageable pageable) {
        Page<Order> page = repository.findAllByExpertId(id, pageable);
        return convert2OutDto(page);
    }

    private CustomPage<OrderFinishOutDto> convert2OutDto(Page<Order> page) {
        List<OrderFinishOutDto> data = page.getContent().stream().map(o -> convert2OutDto(o)).toList();
        CustomPage<OrderFinishOutDto> result = CustomPage.<OrderFinishOutDto>builder()
                .data(data)
                .build();
        return result.convert(page);
    }

    private OrderFinishOutDto convert2OutDto(Order o) {

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

    public CustomPage<OrderOfUserOutDto> findOrdersByCustomer(Long id, Pageable pageable) {
        Page<Order> page = repository.findAllByCustomerId(id, pageable);
        return convert2CustomPage(page, id);
    }

    public CustomPage<OrderOfUserOutDto> getOrdersOfExpert(Long id, Pageable pageable) {
        Page<Order> page = repository.findAllByExpertId(id, pageable);
        return convert2CustomPage(page, 0L);
    }

    public CustomPage<OrderOfUserOutDto> getOrdersByRangeOfTime(Date time1, Date time2, Pageable pageable) {
        Page<Order> page = repository.findOrdersByRangeOfTime(time1, time2, pageable);
        return convert2CustomPage(page, 0L);
    }

    private CustomPage<OrderOfUserOutDto> convert2CustomPage(Page<Order> page, Long userId) {
        List<Order> orders = page.getContent();
        List<OrderOfUserOutDto> data = orders.stream()
                .map(o -> {
                    Long id = userId;
                    if (userId == 0L)
                        id = o.getRequest().getOwner().getId();
                    Suggestion suggestion = suggestionService.findAcceptedByOrderId(o.getId());
                    return OrderOfUserOutDto.builder()
                            .customerId(id)
                            .requestId(o.getRequest().getId())
                            .orderId(o.getId())
                            .expertId(suggestion.getOwner().getId())
                            .suggestionId(suggestion.getId())
                            .mainService(o.getService().getParent().getName())
                            .subService(o.getService().getName())
                            .status(o.getStatus())
                            .suggestionStatus(suggestion.getSuggestionStatus())
                            .price(suggestion.getPrice())
                            .createdTime(o.getCreatedTime())
                            .finishTime(o.getFinishDateTime())
                            .build();
                }).toList();
        CustomPage<OrderOfUserOutDto> result = new CustomPage().convert(page);
        result.setData(data);
        return result;
    }
}
