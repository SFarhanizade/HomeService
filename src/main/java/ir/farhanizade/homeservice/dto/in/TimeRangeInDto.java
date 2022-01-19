package ir.farhanizade.homeservice.dto.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeRangeInDto {
    private Date time1;
    private Date time2;
}
