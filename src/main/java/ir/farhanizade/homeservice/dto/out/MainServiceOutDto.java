package ir.farhanizade.homeservice.dto.out;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MainServiceOutDto {
    private Long id;
    private String name;
    private List<ServiceOutDto> subServices;
}
