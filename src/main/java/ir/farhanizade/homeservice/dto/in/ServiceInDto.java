package ir.farhanizade.homeservice.dto.in;

import ir.farhanizade.homeservice.entity.service.MainService;
import ir.farhanizade.homeservice.entity.service.SubService;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServiceInDto {
    private Long parent;
    private String name;
    private String description;
    private Long basePrice;

    public MainService convert2MainService(){
        return MainService.builder().name(name).build();
    }

    public SubService convert2SubService(){
        return SubService.builder()
                .name(name)
                .description(description)
                .basePrice(new BigDecimal(basePrice))
                .build();
    }
}
