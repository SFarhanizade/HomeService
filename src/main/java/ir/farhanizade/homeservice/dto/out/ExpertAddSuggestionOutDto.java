package ir.farhanizade.homeservice.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpertAddSuggestionOutDto {
    private Long expertId;
    private Long orderId;
    private Long suggestionId;
}
