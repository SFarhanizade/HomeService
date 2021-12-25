package ir.farhanizade.homeservice.entity.order.message;

import ir.farhanizade.homeservice.entity.core.BaseEntity;
import ir.farhanizade.homeservice.entity.core.BasePerson;
import ir.farhanizade.homeservice.entity.order.Order;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import java.math.BigDecimal;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@MappedSuperclass
public class BaseMessage<T extends BasePerson> extends BaseEntity {

    @OneToOne
    @Column(nullable = false)
    private Order order;

    @ManyToOne
    @Column(nullable = false)
    private T owner;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Date dateTime;

    @Column(nullable = false)
    private Date suggestedDateTime;

    private String details;

    @Builder.Default
    @Column(nullable = false)
    private BaseMessageStatus status = BaseMessageStatus.HELD;
}
