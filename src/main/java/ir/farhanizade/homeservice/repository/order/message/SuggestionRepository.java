package ir.farhanizade.homeservice.repository.order.message;

import ir.farhanizade.homeservice.entity.order.message.Suggestion;
import ir.farhanizade.homeservice.entity.order.message.SuggestionStatus;
import ir.farhanizade.homeservice.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SuggestionRepository extends BaseRepository<Suggestion> {

    @Query("From Suggestion s where s.order.id=:orderId")
    Page<Suggestion> findAllByOrderId(Long orderId, Pageable pageable);

    @Query("From Suggestion s where s.owner.id=:ownerId")
    Page<Suggestion> findAllByOwnerId(Long ownerId, Pageable pageable);

    @Query("From Suggestion s where s.order.request.owner.id=:customerId")
    Page<Suggestion> findAllByCustomerId(Long customerId, Pageable pageable);

    @Query("From Suggestion s where s.owner.id=:ownerId and s.suggestionStatus in :status")
    Page<Suggestion> findAllByOwnerIdAndStatus(Long ownerId, SuggestionStatus[] status, Pageable pageable);

    @Query("From Suggestion s where s.id=:id and s.owner.id=:ownerId")
    Optional<Suggestion> findByIdAndOwnerId(Long id, Long ownerId);

    @Query("From Suggestion s where s.suggestionStatus=:accepted and s.order.id=:id")
    Optional<Suggestion> findByStatusAndOrderId(SuggestionStatus accepted, Long id);

    @Query("From Suggestion s where s.order.id=:id and s.suggestionStatus='ACCEPTED'")
    Optional<Suggestion> findAcceptedByOrderId(Long id);
}
