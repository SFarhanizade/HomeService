package ir.farhanizade.homeservice.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionOutDto {
    private Long id;
    private Long expertId;
    private String expertName;
    private Long customerId;
    private String customerName;
    private BigDecimal amount;
    private Date dateTime;
}
