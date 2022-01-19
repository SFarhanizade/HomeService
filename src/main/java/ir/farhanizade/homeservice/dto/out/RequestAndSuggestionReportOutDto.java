package ir.farhanizade.homeservice.dto.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestAndSuggestionReportOutDto {
    Long requests;
    Long suggestions;
}
