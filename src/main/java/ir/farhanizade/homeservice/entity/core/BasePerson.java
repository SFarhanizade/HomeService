package ir.farhanizade.homeservice.entity.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import javax.persistence.MappedSuperclass;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
public class BasePerson extends BaseEntity{
    private String fName;
    private String lName;
    private String email;
    private String password;
}
