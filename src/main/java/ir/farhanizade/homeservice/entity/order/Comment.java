package ir.farhanizade.homeservice.entity.order;

import ir.farhanizade.homeservice.entity.core.BaseEntity;
import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.entity.user.Expert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@EqualsAndHashCode(callSuper = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public class Comment extends BaseEntity {
    @Column(nullable = false)
    private Integer points;
    private String description;

    @ManyToOne
    @Column(nullable = false)
    private Customer sender;

    @ManyToOne
    @Column(nullable = false)
    private Expert recipient;

    @OneToOne
    @Column(nullable = false)
    private Order order;
}
