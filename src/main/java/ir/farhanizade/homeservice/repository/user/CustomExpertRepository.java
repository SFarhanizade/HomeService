package ir.farhanizade.homeservice.repository.user;

import ir.farhanizade.homeservice.dto.in.UserSearchInDto;
import ir.farhanizade.homeservice.entity.user.Expert;

import java.util.List;

public interface CustomExpertRepository {
    List<Expert> search(UserSearchInDto user);
}
