package ir.farhanizade.homeservice.repository.order.message;

import ir.farhanizade.homeservice.entity.order.message.Suggestion;
import ir.farhanizade.homeservice.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SuggestionRepository extends BaseRepository<Suggestion> {

    @Query("From Suggestion s where s.order.id=:orderId")
    List<Suggestion> findAllByOrderId(Long orderId);

    @Query("From Suggestion s where s.owner.id=:ownerId")
    List<Suggestion> findAllByOwnerId(Long ownerId);

    @Query("From Suggestion s where s.order.request.owner.id=:customerId")
    List<Suggestion> findAllByCustomerId(Long customerId);
}
