package ir.farhanizade.homeservice.dto.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
public class ExpertAddSuggestionInDto {
    private Long orderId;
    private Long price;
    private Date dateTime;
    private String details;
    private Double duration;
}
