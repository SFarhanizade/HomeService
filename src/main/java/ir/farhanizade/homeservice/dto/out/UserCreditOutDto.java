package ir.farhanizade.homeservice.dto.out;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
public class UserCreditOutDto {
    private Long id;
    private BigDecimal credit;
}
