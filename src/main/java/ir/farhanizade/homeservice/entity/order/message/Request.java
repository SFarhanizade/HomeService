package ir.farhanizade.homeservice.entity.order.message;

import ir.farhanizade.homeservice.entity.user.Customer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public class Request extends BaseMessage {
    @Column(nullable = false)
    private String address;
}
