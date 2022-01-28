package ir.farhanizade.homeservice.repository;

import ir.farhanizade.homeservice.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TransactionRepository extends BaseRepository<Transaction> {
    @Query("From Transaction t where t.payer.id=:id or t.recipient.id=:id")
    Page<Transaction> findByUserId(Long id, Pageable pageable);

    @Query("From Transaction t where t.id=:transaction and (t.payer.id=:id or t.recipient.id=:id)")
    Optional<Transaction> findByIdAndOwnerId(Long transaction, Long id);
}
