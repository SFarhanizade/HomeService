package ir.farhanizade.homeservice.repository.user;

import ir.farhanizade.homeservice.entity.user.MyUser;
import ir.farhanizade.homeservice.entity.user.UserStatus;
import ir.farhanizade.homeservice.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.Optional;

public interface UserRepository extends BaseRepository<MyUser>, CustomUserRepository {

    @Query("select Count(c) From UserCustomer c where c.createdTime between :time1 and :time2")
    Long getNumberOfCustomersByRegisterTime(Date time1, Date time2);

    @Query("select Count(e) From UserExpert e where e.createdTime between :time1 and :time2")
    Long getNumberOfExpertsByRegisterTime(Date time1, Date time2);

    @Query("From MyUser u where u.email=:username")
    Optional<UserDetails> findByUsername(String username);

    @Query("Select u.id From MyUser u Where u.email=:username")
    Optional<Long> getIdByUsername(String username);

    @Query("Select u.status From MyUser u where u.id=:id")
    Optional<UserStatus> getStatusById(Long id);

    @Query("Select u.status From MyUser u where u.email=:username")
    Optional<UserStatus> getStatusByUsername(String username);
}
