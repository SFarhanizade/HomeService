package ir.farhanizade.homeservice.dto.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.Size;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestInDto {

    @NonNull
    private Long serviceId;
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
