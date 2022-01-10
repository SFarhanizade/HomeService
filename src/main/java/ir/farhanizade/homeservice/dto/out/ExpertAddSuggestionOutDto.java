package ir.farhanizade.homeservice.dto.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExpertAddSuggestionOutDto {
    private Long expertId;
    private Long orderId;
    private Long suggestionId;
}
