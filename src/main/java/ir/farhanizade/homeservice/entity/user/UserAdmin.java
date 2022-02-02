package ir.farhanizade.homeservice.entity.user;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;

//@EqualsAndHashCode(callSuper = true)
//@Data
@Setter
@Getter
@Entity
@AllArgsConstructor
@SuperBuilder
public class UserAdmin extends MyUser {
}
