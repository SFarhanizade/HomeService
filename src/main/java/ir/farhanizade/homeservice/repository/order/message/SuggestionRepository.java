package ir.farhanizade.homeservice.repository.order.message;

import ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus;
import ir.farhanizade.homeservice.entity.order.message.Suggestion;
import ir.farhanizade.homeservice.entity.order.message.SuggestionStatus;
import ir.farhanizade.homeservice.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SuggestionRepository extends BaseRepository<Suggestion> {

    @Query("From Suggestion s where s.order.id=:orderId")
    List<Suggestion> findAllByOrderId(Long orderId);

    @Query("From Suggestion s where s.owner.id=:ownerId")
    List<Suggestion> findAllByOwnerId(Long ownerId);

    @Query("From Suggestion s where s.order.request.owner.id=:customerId")
    List<Suggestion> findAllByCustomerId(Long customerId);

    @Modifying
    @Query("Update Suggestion s set s.suggestionStatus=:accepted where s.id=:suggestionId")
    void acceptSuggestion(Long suggestionId, SuggestionStatus accepted);

    @Modifying
    @Query("Update Suggestion s set s.suggestionStatus=:rejected where Not s.id=:suggestionId and s.order.id=:orderId")
    void rejectOtherSuggestions(Long suggestionId, Long orderId, SuggestionStatus rejected);

    @Query("From Suggestion s where s.owner.id=:ownerId and s.suggestionStatus in :status")
    List<Suggestion> findAllByOwnerIdAndStatus(Long ownerId,SuggestionStatus[] status);

    @Modifying
    @Query("Update Suggestion s set s.status=:cancelled where s.order.id=:orderId")
    void cancel(Long orderId, BaseMessageStatus cancelled);
}
