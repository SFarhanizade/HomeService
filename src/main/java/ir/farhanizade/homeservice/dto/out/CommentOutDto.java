package ir.farhanizade.homeservice.dto.out;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class CommentOutDto {
    private Long id;
    private Long customerId;
    private String customerName;
    private Long expertId;
    private String expertName;
    private Long orderId;
    private Date dateTime;
}
