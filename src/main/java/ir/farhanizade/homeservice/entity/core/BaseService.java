package ir.farhanizade.homeservice.entity.core;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

//@EqualsAndHashCode(callSuper = true)
//@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@MappedSuperclass
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class BaseService extends BaseEntity {
    @Column(nullable = false)
    private String name;
}
