package ir.farhanizade.homeservice.dto.out;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ServiceOutDto {
    private Long id;
    private String name;
    private BigDecimal price;
}
