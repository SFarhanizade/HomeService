package ir.farhanizade.homeservice.repository;

import ir.farhanizade.homeservice.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface TransactionRepository extends BaseRepository<Transaction>{
    @Query("From Transaction t where t.payer.id=:id or t.recipient.id=:id")
    Page<Transaction> findByUserId(Long id, Pageable pageable);
}
