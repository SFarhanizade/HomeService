package ir.farhanizade.homeservice.entity.order.message;

import ir.farhanizade.homeservice.entity.core.BaseEntity;
import ir.farhanizade.homeservice.entity.order.ServiceOrder;
import ir.farhanizade.homeservice.entity.user.User;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

//@EqualsAndHashCode(callSuper = true)
//@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@MappedSuperclass

public class BaseMessage extends BaseEntity {

    @OneToOne
    private ServiceOrder order;

    @ManyToOne
    private User owner;

    @Column(nullable = false)
    private BigDecimal price;

    @Builder.Default
    private Date dateTime = new Date(System.currentTimeMillis());

    @Column(nullable = false)
    private Date suggestedDateTime;

    private String details;


    @Column(nullable = false)
    @Builder.Default
    private BaseMessageStatus status = BaseMessageStatus.WAITING;
}
