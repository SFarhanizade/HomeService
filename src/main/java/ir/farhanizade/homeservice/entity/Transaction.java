package ir.farhanizade.homeservice.entity;

import ir.farhanizade.homeservice.entity.core.BaseEntity;
import ir.farhanizade.homeservice.entity.order.Order;
import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.entity.user.Expert;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(
        uniqueConstraints = {@UniqueConstraint(columnNames = {"order_id"})}
)
public class Transaction extends BaseEntity {
    @OneToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Order order;
    private BigDecimal amount;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Customer payer;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Expert recipient;
}
