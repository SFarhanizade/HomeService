package ir.farhanizade.homeservice.repository.user;

import ir.farhanizade.homeservice.entity.user.UserExpert;
import ir.farhanizade.homeservice.entity.user.UserStatus;
import ir.farhanizade.homeservice.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;


public interface ExpertRepository extends BaseRepository<UserExpert>, CustomExpertRepository {
    UserExpert findByEmail(String email);

    Page<UserExpert> findByCredit(BigDecimal credit, Pageable pageable);

    Page<UserExpert> findByStatus(UserStatus status, Pageable pageable);

    @Query("From UserExpert e inner join e.expertises s where e.id=:service")
    Page<UserExpert> findByExpertise(Long service, Pageable pageable);
}
