package ir.farhanizade.homeservice.dto.out;

import ir.farhanizade.homeservice.entity.order.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class OrderFinishOutDto {
    private Long id;
    private String service;
    private BigDecimal price;
    private Date startDateTime;
    private Date finishDateTime;
    private OrderStatus status;
}
