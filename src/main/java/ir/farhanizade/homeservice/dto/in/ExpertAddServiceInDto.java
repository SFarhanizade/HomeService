package ir.farhanizade.homeservice.dto.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpertAddServiceInDto {
    private Long expertId;
    private Long serviceId;
}
