package ir.farhanizade.homeservice;

import ir.farhanizade.homeservice.entity.core.BasePerson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
@AllArgsConstructor
@Data
@SuperBuilder
public class User extends BasePerson {
}
