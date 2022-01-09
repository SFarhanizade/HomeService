package ir.farhanizade.homeservice.repository.user;

import ir.farhanizade.homeservice.dto.in.UserSearchInDto;
import ir.farhanizade.homeservice.entity.user.User;

import java.util.List;

public interface CustomUserRepository {
    List<User> search(UserSearchInDto user);
}
