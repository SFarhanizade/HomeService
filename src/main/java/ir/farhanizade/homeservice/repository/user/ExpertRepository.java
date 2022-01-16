package ir.farhanizade.homeservice.repository.user;

import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.entity.user.UserStatus;
import ir.farhanizade.homeservice.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;


public interface ExpertRepository extends BaseRepository<Expert>, CustomExpertRepository {
    Expert findByEmail(String email);

    Page<Expert> findByCredit(BigDecimal credit, Pageable pageable);

    Page<Expert> findByStatus(UserStatus status, Pageable pageable);

    @Query("From Expert e inner join e.expertises s where e.id=:service")
    Page<Expert> findByExpertise(Long service, Pageable pageable);

    @Modifying
    @Query("Update Expert e set e.status=:accepted where e.id=:id")
    void acceptExpert(Long id, UserStatus accepted);
}
