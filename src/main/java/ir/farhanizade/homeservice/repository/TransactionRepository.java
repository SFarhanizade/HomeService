package ir.farhanizade.homeservice.repository;

import ir.farhanizade.homeservice.entity.MyTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TransactionRepository extends BaseRepository<MyTransaction> {
    @Query("From MyTransaction t where t.payer.id=:id or t.recipient.id=:id")
    Page<MyTransaction> findByUserId(Long id, Pageable pageable);

    @Query("From MyTransaction t where t.id=:transaction and (t.payer.id=:id or t.recipient.id=:id)")
    Optional<MyTransaction> findByIdAndOwnerId(Long transaction, Long id);
}
