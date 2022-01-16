package ir.farhanizade.homeservice.repository.order;

import ir.farhanizade.homeservice.entity.order.Order;
import ir.farhanizade.homeservice.entity.order.OrderStatus;
import ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus;
import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OrderRepository extends BaseRepository<Order>, CustomOrderRepository {

    @Query("From Order o " +
            "where o.service in :expertises " +
            "and (o.status=:forSuggestion or o.status=:forSelection)" +
            "and not (o.request.status=:cancelled or o.request.status=:busy)")
    Page<Order> loadByExpertises(Set<SubService> expertises,
                                 OrderStatus forSuggestion, OrderStatus forSelection,
                                 BaseMessageStatus cancelled, BaseMessageStatus busy, Pageable pageable);

    @Query("From Order o where o.id=:orderId and o.request.owner.id=:ownerId")
    Optional<Order> findByIdAndCustomerId(Long ownerId, Long orderId);

    @Query("From Order o where o.request.owner.id=:ownerId")
    Page<Order> findAllByCustomerId(Long ownerId,Pageable pageable);

    @Modifying
    @Query("Update Request r set r.status=:cancelled where r.owner.id=:owner_id and r.order.id=:order_id")
    void removeOrderByIdAndOwnerId(Long order_id, Long owner_id, BaseMessageStatus cancelled);

    @Modifying
    @Query("Update Order o set o.status=:status where o.id=:id")
    void changeStatus(Long id, OrderStatus status);

    @Query("From Order o  inner join o.suggestions s where s.owner.id=:id")
    Page<Order> findAllByExpertId(Long id, Pageable pageable);
}
