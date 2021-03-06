package ir.farhanizade.homeservice.entity.order.message;

import ir.farhanizade.homeservice.entity.core.BaseEntity;
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
@EqualsAndHashCode(callSuper = false, of = {"order"})
public class BaseMessage extends BaseEntity {


    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Date suggestedDateTime;

    private String details;


    @Column(nullable = false)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private BaseMessageStatus status = BaseMessageStatus.WAITING;
}
