package ir.farhanizade.homeservice.dto.in;

import lombok.Data;

import java.util.Date;

@Data
public class ExpertAddSuggestionInDto {
    private Long orderId;
    private Long price;
    private Date dateTime;
    private String details;
    private Double duration;
}
