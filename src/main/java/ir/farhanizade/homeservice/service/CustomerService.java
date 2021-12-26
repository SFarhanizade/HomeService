package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.entity.user.UserStatus;
import ir.farhanizade.homeservice.repository.user.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository repository;

    @Transactional
    public void save(Customer customer) {
        repository.save(customer);
    }

    public Customer findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public Customer findByCredit(BigDecimal credit) {
        return repository.findByCredit(credit);
    }

    public Customer findByStatus(UserStatus status) {
        return repository.findByStatus(status);
    }
}
