package ir.farhanizade.homeservice.dto.out;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class OrderOutDto {
    private Long id;
    private String service;
    private BigDecimal price;
    private Date suggestedDateTime;
    private Date createdDateTime;
}