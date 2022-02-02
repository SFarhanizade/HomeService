package ir.farhanizade.homeservice.entity;

import ir.farhanizade.homeservice.entity.core.BaseEntity;
import ir.farhanizade.homeservice.entity.order.MyOrder;
import ir.farhanizade.homeservice.entity.user.UserCustomer;
import ir.farhanizade.homeservice.entity.user.UserExpert;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(
        uniqueConstraints = {@UniqueConstraint(columnNames = {"my_order_id"})}
)
public class MyTransaction extends BaseEntity {
    @OneToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private MyOrder myOrder;
    private BigDecimal amount;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private UserCustomer payer;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private UserExpert recipient;
}
