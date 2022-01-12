package ir.farhanizade.homeservice.repository.order.message;

import ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus;
import ir.farhanizade.homeservice.entity.order.message.Request;
import ir.farhanizade.homeservice.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RequestRepository extends BaseRepository<Request> {
    List<Request> findByStatus(BaseMessageStatus waiting);

    @Modifying
    @Query("Update Request r set r.status=:cancelled where r.order.id=:orderId")
    void changeStatus(Long orderId, BaseMessageStatus cancelled);
}
