package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.entity.order.Order;
import ir.farhanizade.homeservice.entity.order.OrderStatus;
import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.exception.EntityNotFoundException;
import ir.farhanizade.homeservice.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repository;

    public void save(Order order){
        repository.save(order);
    }

    public List<Order> loadByExpertises(Set<SubService> expertises, OrderStatus status) throws EntityNotFoundException {
        List<Order> orders = repository.loadByExpertises(expertises, status);
        if(orders.size() == 0){
            throw new EntityNotFoundException("No Orders Found!");
        }
        return orders;
    }

    public List<Order> loadAll(){
        return repository.findAll();
    }

    public Order findById(Long id) throws EntityNotFoundException {
        Optional<Order> byId = repository.findById(id);
        if(byId.isPresent()){
            return byId.get();
        } else throw new EntityNotFoundException("Order Doesn't Exist!");
    }
}
