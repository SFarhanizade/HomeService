package ir.farhanizade.homeservice.repository.user;

import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.entity.user.UserStatus;
import ir.farhanizade.homeservice.repository.BaseRepository;

import java.math.BigDecimal;

public interface CustomerRepository extends BaseRepository<Customer> {
    Customer findByEmail(String email);
    Customer findByCredit(BigDecimal credit);
    Customer findByStatus(UserStatus status);

}
