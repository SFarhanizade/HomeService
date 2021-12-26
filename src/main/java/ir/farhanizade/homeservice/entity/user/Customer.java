package ir.farhanizade.homeservice.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
@AllArgsConstructor
//@NoArgsConstructor
@Data
@SuperBuilder
public class Customer extends User {

    /*@OneToMany
    private List<Order> orders;*/


}
