package ir.farhanizade.homeservice.entity.order.message;

import ir.farhanizade.homeservice.entity.order.Order;
import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.entity.user.User;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
        uniqueConstraints = {@UniqueConstraint(columnNames = {"order_id", "owner_id"})}
)
public class Request extends BaseMessage {

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToOne(cascade = CascadeType.ALL)
    protected Order order;

    @ManyToOne(cascade = CascadeType.ALL)
    private Customer owner;

    @Column(nullable = false)
    private String address;
}
