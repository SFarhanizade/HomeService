package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.entity.user.UserStatus;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.repository.user.CustomerRepository;
import ir.farhanizade.homeservice.service.util.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository repository;

    @Transactional
    public void save(Customer customer) throws UserNotValidException, DuplicateEntityException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException {
        boolean isValid = false;
        isValid = Validation.isValid(customer);

        if (!isValid)
            throw new UserNotValidException("");

        if (finalCheck(customer))
            throw new DuplicateEntityException("");
        repository.save(customer);
    }

    public Customer findByEmail(String email) {
        if (email == null)
            throw new IllegalStateException("Null Email");
        return repository.findByEmail(email);
    }

    public List<Customer> findByCredit(BigDecimal credit) {
        return repository.findByCredit(credit);
    }

    public List<Customer> findByStatus(UserStatus status) {
        return repository.findByStatus(status);
    }

    public List<Customer> findAll() {
        return repository.findAll();
    }

    private boolean finalCheck(Customer customer) {
        String email = customer.getEmail();
        Customer byEmail = repository.findByEmail(email);
        return byEmail != null && customer.getId() == null;
    }
}
