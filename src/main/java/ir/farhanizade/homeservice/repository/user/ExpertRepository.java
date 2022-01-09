package ir.farhanizade.homeservice.repository.user;

import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.entity.user.UserStatus;
import ir.farhanizade.homeservice.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;


public interface ExpertRepository extends BaseRepository<Expert>, CustomExpertRepository{
    Expert findByEmail(String email);
    List<Expert> findByCredit(BigDecimal credit);
    List<Expert> findByStatus(UserStatus status);

    @Query(value = "select u.* From user_expertises e, user u where e.expertises_id = :service and e.expert_id = u.id", nativeQuery = true)
    List<Expert> findByExpertise(Long service);
}
