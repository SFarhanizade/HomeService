package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.dto.out.OrderOutDto;
import ir.farhanizade.homeservice.entity.order.Order;
import ir.farhanizade.homeservice.entity.order.message.Request;
import ir.farhanizade.homeservice.entity.order.message.Suggestion;
import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.repository.order.OrderRepository;
import ir.farhanizade.homeservice.service.util.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    public void save(Order order) {
        repository.save(order);
    }

    @Transactional(readOnly = true)
    public List<Order> loadByExpertises(Set<SubService> expertises) throws EntityNotFoundException {
        List<Order> orders = repository.loadByExpertises(expertises, WAITING_FOR_SUGGESTION, WAITING_FOR_SELECTION, CANCELLED, BUSY);
        if (orders.size() == 0) {
            throw new EntityNotFoundException("No Orders Found!");
        }
        return orders;
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
                    .createdDateTime(result.getRequest().getDateTime())
                    .status(result.getStatus())
                    .build();
        }
        throw new EntityNotFoundException("Order Doesn't Exist!");
    }

    @Transactional(readOnly = true)
    public List<Order> findAllByCustomerId(Long ownerId) {
        return repository.findAllByCustomerId(ownerId);
    }

    @Transactional
    public void removeOrderByIdAndOwnerId(Long orderId, Long ownerId) throws EntityNotFoundException {
        Order order = findById(orderId);
        Request request = order.getRequest();
        if (!request.getOwner().getId().equals(ownerId)) throw new EntityNotFoundException("This order is not yours!");
        request.setStatus(CANCELLED);
        Set<Suggestion> suggestions = order.getSuggestions();
        suggestions.stream()
                .forEach(s -> {
                    s.setSuggestionStatus(REJECTED);
                    s.setStatus(CANCELLED);
                });
        repository.save(order);
    }

    @Transactional
    public EntityOutDto acceptSuggestion(Long id) throws BusyOrderException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException, BadEntryException, EntityNotFoundException, DuplicateEntityException {
        Suggestion suggestion = suggestionService.findById(id);
        Validation.isValid(suggestion);
        Order order = suggestion.getOrder();
        order.setStatus(WAITING_FOR_EXPERT);
        suggestion.setSuggestionStatus(ACCEPTED);
        Set<Suggestion> suggestions = order.getSuggestions();
        suggestions.stream()
                .filter(s -> !s.equals(suggestion))
                .forEach(s -> s.setSuggestionStatus(REJECTED));
        suggestionService.save(suggestion);
        return new EntityOutDto(id);
    }
}
