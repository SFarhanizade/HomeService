package ir.farhanizade.homeservice.repository.user;

import ir.farhanizade.homeservice.dto.in.UserSearchInDto;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.user.Expert;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomExpertRepository {
    CustomPage<Expert> search(UserSearchInDto user, Pageable pageable);
}
