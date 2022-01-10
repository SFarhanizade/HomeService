package ir.farhanizade.homeservice.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
public class SuggestionOutDto {
    private Long id;
    private String ownerName;
    private Long ownerId;
    private Integer ownerPoints;
    private BigDecimal price;
    private Double duration;
    private String details;
    private Date suggestedDateTime;
    private Date createdDateTime;
}
