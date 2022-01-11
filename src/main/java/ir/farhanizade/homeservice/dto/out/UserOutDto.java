package ir.farhanizade.homeservice.dto.out;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class UserOutDto {
    private Long id;
    private String name;
    private String email;
    private BigDecimal credit;
}
