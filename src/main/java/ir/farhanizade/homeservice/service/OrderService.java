package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.entity.order.Order;
import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repository;

    public void save(Order order){
        repository.save(order);
    }

    public List<Order> loadByExpertises(List<SubService> expertises){
        return repository.loadByExpertises(expertises);
    }

    public List<Order> loadAll(){
        return repository.findAll();
    }
}
