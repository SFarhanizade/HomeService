package ir.farhanizade.homeservice.dto.out;

import ir.farhanizade.homeservice.entity.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderOutDto {
    private Long id;
    private String service;
    private BigDecimal price;
    private Date suggestedDateTime;
    private Date createdDateTime;
    private OrderStatus status;
}
