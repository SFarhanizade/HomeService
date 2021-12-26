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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository repository;

    @Transactional
    public void save(Customer customer) throws UserNotValidException, DuplicateEntityException, NameNotValidException, EmailNotValidException, PasswordNotValidException {
        boolean isValid = false;
            isValid = Validation.isValid(customer);

        if(!isValid)
            throw new UserNotValidException("");
        String email = customer.getEmail();
        Customer byEmail = repository.findByEmail(email);
        if(byEmail!=null)
            throw new DuplicateEntityException("");
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
