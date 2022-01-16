package ir.farhanizade.homeservice.dto.out;

import ir.farhanizade.homeservice.entity.order.OrderStatus;
import ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class RequestOutDto {
    private String service;
    private Date suggestedDateTime;
    private Date createdDateTime;
    private BigDecimal price;
    private BaseMessageStatus status;
    private OrderStatus orderStatus;
}
