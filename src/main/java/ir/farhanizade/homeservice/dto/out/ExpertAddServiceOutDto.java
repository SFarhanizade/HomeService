package ir.farhanizade.homeservice.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ExpertAddServiceOutDto {
    private Long expertId;
    private List<EntityOutDto> services;
}
