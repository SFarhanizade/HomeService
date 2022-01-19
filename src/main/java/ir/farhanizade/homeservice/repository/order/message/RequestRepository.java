package ir.farhanizade.homeservice.repository.order.message;

import ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus;
import ir.farhanizade.homeservice.entity.order.message.Request;
import ir.farhanizade.homeservice.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends BaseRepository<Request> {
    Page<Request> findByStatus(BaseMessageStatus waiting, Pageable pageable);

    @Query("From Request r where r.order.id=:orderId")
    Optional<Request> findByOrderId(Long orderId);

    @Query("Select Count(r) From Request r")
    Long countNumberOfRequests();
}
