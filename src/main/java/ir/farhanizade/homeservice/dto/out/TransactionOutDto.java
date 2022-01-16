package ir.farhanizade.homeservice.dto.out;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Builder
@Data
public class TransactionOutDto {
    private Long id;
    private Long expertId;
    private String expertName;
    private Long customerId;
    private String customerName;
    private BigDecimal amount;
    private Date dateTime;
}
