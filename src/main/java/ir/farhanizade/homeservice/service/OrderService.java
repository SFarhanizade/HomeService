package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.entity.order.ServiceOrder;
import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repository;

    public void save(ServiceOrder order){
        repository.save(order);
    }

    public List<ServiceOrder> loadByExpertises(List<SubService> expertises){
        return repository.loadByExpertises(expertises);
    }

    public List<ServiceOrder> loadAll(){
        return repository.findAll();
    }
}
