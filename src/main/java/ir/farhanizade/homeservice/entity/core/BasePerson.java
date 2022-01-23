package ir.farhanizade.homeservice.entity.core;

import com.sun.istack.Nullable;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

//@EqualsAndHashCode(callSuper = true)
//@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@MappedSuperclass
public class BasePerson extends BaseEntity {
    @Column(nullable = false)
    private String fName;
    @Column(nullable = false)
    private String lName;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;

    public String getName() {
        return String.format("%s %s", fName, lName);
    }
}
