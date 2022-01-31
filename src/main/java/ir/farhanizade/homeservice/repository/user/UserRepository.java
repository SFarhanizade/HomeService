package ir.farhanizade.homeservice.repository.user;

import ir.farhanizade.homeservice.entity.user.User;
import ir.farhanizade.homeservice.entity.user.UserStatus;
import ir.farhanizade.homeservice.repository.BaseRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.Optional;

public interface UserRepository extends BaseRepository<User>, CustomUserRepository {

    @Query("select Count(c) From Customer c where c.createdTime between :time1 and :time2")
    Long getNumberOfCustomersByRegisterTime(Date time1, Date time2);

    @Query("select Count(e) From Expert e where e.createdTime between :time1 and :time2")
    Long getNumberOfExpertsByRegisterTime(Date time1, Date time2);

    @Query("From User u where u.email=:username")
    Optional<UserDetails> findByUsername(String username);

    @Query("Select u.id From User u Where u.email=:username")
    Optional<Long> getIdByUsername(String username);

    @Query("Select u.status From User u where u.id=:id")
    Optional<UserStatus> getStatusById(Long id);

    @Query("Select u.status From User u where u.email=:username")
    Optional<UserStatus> getStatusByUsername(String username);
}
