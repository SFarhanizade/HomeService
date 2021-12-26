package ir.farhanizade.homeservice.repository.user;

import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.entity.user.UserStatus;
import ir.farhanizade.homeservice.repository.BaseRepository;

import java.math.BigDecimal;


public interface ExpertRepository extends BaseRepository<Expert> {
    Expert findByEmail(String email);
    Expert findByCredit(BigDecimal credit);
    Expert findByStatus(UserStatus status);
    Expert findByExpertise(SubService service);
}
