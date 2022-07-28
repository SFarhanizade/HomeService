package ir.farhanizade.homeservice.dto.in;

import ir.farhanizade.homeservice.entity.service.MyService;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
public class ServiceInDto {

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

    public ServiceInDto(Long parent, @NonNull String name, @NonNull String description, @NonNull Long basePrice) {
        this.parent = parent;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
    }

    public MyService convert2Service() {
        return MyService.builder()
                .name(name)
                .description(description)
                .basePrice(new BigDecimal(basePrice))
                .build();
    }
}
