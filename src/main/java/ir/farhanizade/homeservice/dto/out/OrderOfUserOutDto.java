package ir.farhanizade.homeservice.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderOfUserOutDto {
    private Long userId;
    private Long orderId;
    private String mainService;
    private String subService;
    private Date createdTime;
    private Date finishTime;
    private Long price;
}
