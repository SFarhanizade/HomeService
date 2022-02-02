package ir.farhanizade.homeservice.repository.user;

import ir.farhanizade.homeservice.entity.user.UserCustomer;
import ir.farhanizade.homeservice.entity.user.UserStatus;
import ir.farhanizade.homeservice.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface CustomerRepository extends BaseRepository<UserCustomer>, CustomCustomerRepository {
    UserCustomer findByEmail(String email);
    Page<UserCustomer> findByCredit(BigDecimal credit, Pageable pageable);
    Page<UserCustomer> findAll(Pageable pageable);
    Page<UserCustomer> findByStatus(UserStatus status, Pageable pageable);

}
