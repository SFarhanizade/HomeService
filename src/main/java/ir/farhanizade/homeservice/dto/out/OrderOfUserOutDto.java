package ir.farhanizade.homeservice.dto.out;

import ir.farhanizade.homeservice.entity.order.OrderStatus;
import ir.farhanizade.homeservice.entity.order.message.SuggestionStatus;
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
public class OrderOfUserOutDto {
    private Long customerId;
    private Long requestId;
    private Long orderId;
    private Long expertId;
    private Long suggestionId;
    private String mainService;
    private String subService;
    private OrderStatus status;
    private SuggestionStatus suggestionStatus;
    private Date createdTime;
    private Date finishTime;
    private BigDecimal price;
}
