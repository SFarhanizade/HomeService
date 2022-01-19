package ir.farhanizade.homeservice.repository.user;

import ir.farhanizade.homeservice.entity.user.User;
import ir.farhanizade.homeservice.repository.BaseRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface UserRepository extends BaseRepository<User>, CustomUserRepository {

    @Query("select Count(c) From Customer c where c.createdTime between :time1 and :time2")
    Long getNumberOfCustomersByRegisterTime(Date time1, Date time2);

    @Query("select Count(e) From Expert e where e.createdTime between :time1 and :time2")
    Long getNumberOfExpertsByRegisterTime(Date time1, Date time2);
}
