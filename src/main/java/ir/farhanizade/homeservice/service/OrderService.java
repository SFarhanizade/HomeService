package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.out.OrderOutDto;
import ir.farhanizade.homeservice.entity.order.Order;
import ir.farhanizade.homeservice.entity.order.OrderStatus;
import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.exception.EntityNotFoundException;
import ir.farhanizade.homeservice.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static ir.farhanizade.homeservice.entity.order.OrderStatus.*;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repository;

    @Transactional
    public void save(Order order) {
        repository.save(order);
    }

    @Transactional(readOnly = true)
    public List<Order> loadByExpertises(Set<SubService> expertises) throws EntityNotFoundException {
        List<Order> orders = repository.loadByExpertises(expertises, WAITING_FOR_SUGGESTION, WAITING_FOR_SELECTION);
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
                    .build();
        }
        throw new EntityNotFoundException("Order Doesn't Exist!");
    }

    @Transactional(readOnly = true)
    public List<Order> findAllByCustomerId(Long ownerId) {
        return repository.findAllByCustomerId(ownerId);
    }

    @Transactional
    public void removeOrderByIdAndOwnerId(Long ownerId, Long orderId) throws EntityNotFoundException {
        exists(orderId);
        repository.removeOrderByIdAndOwnerId(orderId, ownerId);
    }

    @Transactional(readOnly = true)
    public boolean exists(Long orderId) throws EntityNotFoundException {
        boolean exists = repository.existsById(orderId);
        if (exists)
            return true;
        throw new EntityNotFoundException("Order Not Found!");
    }

    public void acceptSuggestion(Long id) {
        repository.acceptSuggestion(id, WAITING_FOR_EXPERT);
    }
}
