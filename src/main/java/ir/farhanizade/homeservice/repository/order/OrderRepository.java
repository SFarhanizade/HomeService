package ir.farhanizade.homeservice.repository.order;

import ir.farhanizade.homeservice.entity.order.Order;
import ir.farhanizade.homeservice.entity.order.OrderStatus;
import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface OrderRepository extends BaseRepository<Order> {

    @Query("From Order o where o.service in :expertises and o.status=:status")
    List<Order> loadByExpertises(Set<SubService> expertises, OrderStatus status);
}
