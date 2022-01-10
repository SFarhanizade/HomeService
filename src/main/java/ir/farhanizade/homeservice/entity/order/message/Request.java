package ir.farhanizade.homeservice.entity.order.message;

import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.entity.user.User;
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
        uniqueConstraints = {@UniqueConstraint(columnNames = {"order", "owner"})}
)
public class Request extends BaseMessage {
    @ManyToOne(cascade = CascadeType.ALL)
    private Customer owner;

    @Column(nullable = false)
    private String address;
}
