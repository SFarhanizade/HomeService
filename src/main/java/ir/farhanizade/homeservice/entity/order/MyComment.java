package ir.farhanizade.homeservice.entity.order;

import ir.farhanizade.homeservice.entity.core.BaseEntity;
import ir.farhanizade.homeservice.entity.user.UserCustomer;
import ir.farhanizade.homeservice.entity.user.UserExpert;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

//@EqualsAndHashCode(callSuper = true)
//@Data
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(
        uniqueConstraints = {@UniqueConstraint(columnNames = {"my_order_id", "my_customer_id", "my_expert_id"})}
)
public class MyComment extends BaseEntity {
    @Column(nullable = false)
    private Integer points;
    private String description;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private UserCustomer myCustomer;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private UserExpert myExpert;

    @OneToOne
    private MyOrder myOrder;
}
