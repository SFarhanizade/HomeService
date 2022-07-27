package ir.farhanizade.homeservice.repository.order;

import ir.farhanizade.homeservice.entity.order.MyOrder;
import ir.farhanizade.homeservice.entity.order.OrderStatus;
import ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus;
import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

public interface OrderRepository extends BaseRepository<MyOrder> {

    @Query("From MyOrder o " +
            "where o.service in :expertises " +
            "and (o.status=:forSuggestion or o.status=:forSelection)" +
            "and not (o.request.status=:cancelled or o.request.status=:busy)")
    Page<MyOrder> loadByExpertises(Set<SubService> expertises,
                                   OrderStatus forSuggestion, OrderStatus forSelection,
                                   BaseMessageStatus cancelled, BaseMessageStatus busy, Pageable pageable);

    @Query("From MyOrder o where o.id=:orderId and o.request.owner.id=:ownerId")
    Optional<MyOrder> findByIdAndCustomerId(Long ownerId, Long orderId);

    @Query("From MyOrder o where o.request.owner.id=:ownerId")
    Page<MyOrder> findAllByCustomerId(Long ownerId, Pageable pageable);

    @Query("From MyOrder o  inner join o.suggestions s where s.owner.id=:id")
    Page<MyOrder> findAllByExpertId(Long id, Pageable pageable);

    @Query("From MyOrder o where o.createdTime between :time1 and :time2")
    Page<MyOrder> findOrdersByRangeOfTime(Date time1, Date time2, Pageable pageable);

    @Query("From MyOrder o where o.status=:status")
    Page<MyOrder> findByStatus(OrderStatus status, Pageable pageable);

    @Query("From MyOrder o where o.service.parent.id=:id")
    Page<MyOrder> findByMainService(Long id, Pageable pageable);

    @Query("From MyOrder o where o.service.id=:id")
    Page<MyOrder> findBySubService(Long id, Pageable pageable);

    @Query("Select Count(o) From MyOrder o where o.status='DONE' or o.status='PAID'")
    Long countDoneOrders();

    @Query("From MyOrder o where o.id=:id and o.request.owner.id=:ownerId")
    Optional<MyOrder> findByIdAndOwnerId(Long id, Long ownerId);
}
