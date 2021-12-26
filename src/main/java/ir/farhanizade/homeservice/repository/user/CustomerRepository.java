package ir.farhanizade.homeservice.repository.user;

import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.entity.user.UserStatus;
import ir.farhanizade.homeservice.repository.BaseRepository;

import java.math.BigDecimal;
import java.util.List;

public interface CustomerRepository extends BaseRepository<Customer> {
    Customer findByEmail(String email);
    List<Customer> findByCredit(BigDecimal credit);
    List<Customer> findByStatus(UserStatus status);

}
