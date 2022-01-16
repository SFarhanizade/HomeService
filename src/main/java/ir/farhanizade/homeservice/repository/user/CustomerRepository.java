package ir.farhanizade.homeservice.repository.user;

import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.entity.user.UserStatus;
import ir.farhanizade.homeservice.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface CustomerRepository extends BaseRepository<Customer>, CustomCustomerRepository {
    Customer findByEmail(String email);
    Page<Customer> findByCredit(BigDecimal credit, Pageable pageable);
    Page<Customer> findAll(Pageable pageable);
    Page<Customer> findByStatus(UserStatus status, Pageable pageable);

}
