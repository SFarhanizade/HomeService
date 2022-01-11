package ir.farhanizade.homeservice.dto.out;

import ir.farhanizade.homeservice.entity.order.message.SuggestionStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Builder
@Data
public class ExpertSuggestionOutDto {
    private Long id;
    private String service;
    private BigDecimal price;
    private Date suggestedDateTime;
    private SuggestionStatus status;
}
