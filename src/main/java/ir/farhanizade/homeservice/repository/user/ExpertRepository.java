package ir.farhanizade.homeservice.repository.user;

import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.entity.user.UserStatus;
import ir.farhanizade.homeservice.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;


public interface ExpertRepository extends BaseRepository<Expert>, CustomExpertRepository{
    Expert findByEmail(String email);
    List<Expert> findByCredit(BigDecimal credit);
    List<Expert> findByStatus(UserStatus status);

    //@Query(value = "select u.* From user_expertises e, user u where e.expertises_id = :service and e.expert_id = u.id", nativeQuery = true)
    @Query("From Expert e inner join e.expertises s where e.id=:service")
    List<Expert> findByExpertise(Long service);

    @Modifying
    @Query("Update Expert e set e.status=:accepted where e.id=:id")
    void acceptExpert(Long id, UserStatus accepted);
}
