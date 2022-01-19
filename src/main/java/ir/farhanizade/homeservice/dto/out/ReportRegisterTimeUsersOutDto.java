package ir.farhanizade.homeservice.dto.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportRegisterTimeUsersOutDto {
    private Long experts;
    private Long customers;
}
