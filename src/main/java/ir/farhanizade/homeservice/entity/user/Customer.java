package ir.farhanizade.homeservice.entity.user;

import ir.farhanizade.homeservice.entity.order.ServiceOrder;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

//@EqualsAndHashCode(callSuper = true)
//@Data
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Customer extends User {

    @OneToMany(cascade = CascadeType.ALL)
    @Builder.Default
    private List<ServiceOrder> orders = new ArrayList<>();


    public void addOrder(ServiceOrder order) {
        orders.add(order);
    }
}
