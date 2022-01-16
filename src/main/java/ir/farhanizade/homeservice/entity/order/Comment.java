package ir.farhanizade.homeservice.entity.order;

import ir.farhanizade.homeservice.entity.core.BaseEntity;
import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.entity.user.Expert;
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
        uniqueConstraints = {@UniqueConstraint(columnNames = {"order_id", "customer_id", "expert_id"})}
)
public class Comment extends BaseEntity {
    @Column(nullable = false)
    private Integer points;
    private String description;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Expert expert;

    @OneToOne
    private Order order;
}
