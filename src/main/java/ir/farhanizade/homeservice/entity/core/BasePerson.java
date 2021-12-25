package ir.farhanizade.homeservice.entity.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import javax.persistence.MappedSuperclass;

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
