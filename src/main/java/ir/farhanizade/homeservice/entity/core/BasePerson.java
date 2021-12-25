package ir.farhanizade.homeservice.entity.core;

import com.sun.istack.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@EqualsAndHashCode(callSuper = true)
@Data
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
}
