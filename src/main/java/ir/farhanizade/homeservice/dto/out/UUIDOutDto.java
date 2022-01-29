package ir.farhanizade.homeservice.dto.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UUIDOutDto extends EntityOutDto {
    private String uuid;
}
