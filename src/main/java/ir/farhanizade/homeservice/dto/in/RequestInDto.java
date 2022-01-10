package ir.farhanizade.homeservice.dto.in;

import ir.farhanizade.homeservice.entity.order.Order;
import ir.farhanizade.homeservice.entity.order.message.Request;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Size;
import java.util.Date;

@Data
public class RequestInDto {

    @NonNull
    private Long serviceId;
    @NonNull
    private Long ownerId;
    @NonNull
    private Long price;
    @NonNull
    private Date suggestedDateTime;
    @NonNull
    @Size(max = 150)
    private String details;
    @NonNull
    @Size(min = 5, max = 100)
    private String address;
}
