package ir.farhanizade.homeservice.repository.user;

import ir.farhanizade.homeservice.dto.in.UserSearchInDto;
import ir.farhanizade.homeservice.entity.user.Customer;

import java.util.List;

public interface CustomCustomerRepository {
    List<Customer> search(UserSearchInDto user);
}
