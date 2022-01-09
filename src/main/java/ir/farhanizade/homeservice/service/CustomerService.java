package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.in.UserInDto;
import ir.farhanizade.homeservice.dto.in.UserSearchInDto;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.dto.out.UserSearchOutDto;
import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.entity.user.Expert;
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
    public EntityOutDto save(UserInDto user) throws UserNotValidException, DuplicateEntityException, NameNotValidException, EmailNotValidException, PasswordNotValidException, NullFieldException {
        Customer customer = user.convert2Customer();
        boolean isValid = false;
        isValid = Validation.isValid(customer);

        if (!isValid)
            throw new UserNotValidException("User is not valid!");

        if (finalCheck(customer))
            throw new DuplicateEntityException("User exists!");
        Customer result = repository.save(customer);
        return new EntityOutDto(result.getId());
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

    public List<UserSearchOutDto> search(UserSearchInDto user) {
        List<Customer> searchResult = repository.search(user);
        List<UserSearchOutDto> result = searchResult.stream()
                .map(e -> new UserSearchOutDto().convert2Dto(e))
                .peek(e -> e.setType("customer"))
                .toList();
        return result;
    }
}
