package ir.farhanizade.homeservice.dto.out;

import ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SuggestionAnswerOutDto {
    private Long suggestion;
    private Long orderId;
    private BaseMessageStatus answer;
}
