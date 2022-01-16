package ir.farhanizade.homeservice.dto.out;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class UserIncreaseCreditOutDto {
    private Long id;
    private Long amount;
    private BigDecimal balance;
}
