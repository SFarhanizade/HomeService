package ir.farhanizade.homeservice.entity;

import ir.farhanizade.homeservice.entity.core.BaseEntity;
import ir.farhanizade.homeservice.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import java.math.BigDecimal;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@MappedSuperclass
public class Transaction extends BaseEntity {
    private BigDecimal amount;
    @ManyToOne(fetch = FetchType.EAGER)
    private User payer;
    @ManyToOne(fetch = FetchType.EAGER)
    private User recipient;
    private Date dateTime;
}
