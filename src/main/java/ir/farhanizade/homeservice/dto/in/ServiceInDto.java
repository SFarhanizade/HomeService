package ir.farhanizade.homeservice.dto.in;

import ir.farhanizade.homeservice.entity.service.MainService;
import ir.farhanizade.homeservice.entity.service.SubService;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
public class ServiceInDto {
    @NonNull
    private Long parent;
    @NonNull
    @Size(min = 3, max = 20)
    private String name;
    @NonNull
    @Size(min = 5, max = 100)
    private String description;
    @NonNull
    @Size(min = 1000)
    private Long basePrice;

    public MainService convert2MainService() {
        return MainService.builder().name(name).build();
    }

    public SubService convert2SubService() {
        return SubService.builder()
                .name(name)
                .description(description)
                .basePrice(new BigDecimal(basePrice))
                .build();
    }
}
