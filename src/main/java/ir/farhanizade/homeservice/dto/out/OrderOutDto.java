package ir.farhanizade.homeservice.dto.out;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class OrderOutDto {
    private Long id;
    private String service;
    private Long price;
    private Date suggestedDateTime;
    private Date createdDateTime;
}
