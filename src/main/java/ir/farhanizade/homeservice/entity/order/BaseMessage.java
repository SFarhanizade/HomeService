package ir.farhanizade.homeservice.entity.order;

import ir.farhanizade.homeservice.entity.core.BaseEntity;
import ir.farhanizade.homeservice.entity.user.User;
import lombok.*;

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
public class BaseMessage extends BaseEntity {

    @OneToOne
    private Order order;

    @ManyToOne
    private User owner;

    private BigDecimal price;

    private Date dateTime;

    private Date suggestedDateTime;

    @Builder.Default
    private BaseMessageStatus status = BaseMessageStatus.HELD;
}
