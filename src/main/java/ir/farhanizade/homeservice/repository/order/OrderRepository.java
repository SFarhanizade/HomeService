package ir.farhanizade.homeservice.repository.order;

import ir.farhanizade.homeservice.entity.order.ServiceOrder;
import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.repository.BaseRepository;
import ir.farhanizade.homeservice.service.OrderService;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends BaseRepository<ServiceOrder> {

    @Query("From ServiceOrder o where o.service in :expertises")
    List<OrderService> loadByExpertises(List<SubService> expertises);
}
