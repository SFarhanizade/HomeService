package ir.farhanizade.homeservice.repository.order;

import ir.farhanizade.homeservice.entity.order.Order;
import ir.farhanizade.homeservice.entity.order.OrderStatus;
import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OrderRepository extends BaseRepository<Order> {

    @Query("From Order o where o.service in :expertises and (o.status=:status1 or o.status=:status2)")
    List<Order> loadByExpertises(Set<SubService> expertises, OrderStatus status1, OrderStatus status2);

    @Query("From Order o where o.id=:orderId and o.request.owner.id=:ownerId")
    Optional<Order> findByIdAndCustomerId(Long ownerId, Long orderId);

    @Query("From Order o where o.request.owner.id=:ownerId")
    List<Order> findAllByCustomerId(Long ownerId);

    @Modifying
    @Query(value = "Delete From my_order " +
            "Where id =(SELECT r.order_id " +
                        "FROM request r " +
                        "WHERE r.owner_id =:owner_id AND r.order_id=:order_id)"
            , nativeQuery = true)
    void removeOrderByIdAndOwnerId(Long order_id, Long owner_id);

    @Modifying
    @Query("Update Order o set o.status=:status where o.id=:id")
    void acceptSuggestion(Long id, OrderStatus status);
}
