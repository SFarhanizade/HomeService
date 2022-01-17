package ir.farhanizade.homeservice.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentOutDto {
    private Long id;
    private Long customerId;
    private String customerName;
    private Long expertId;
    private String expertName;
    private Integer points;
    private String description;
    private Long orderId;
    private Date dateTime;
}
