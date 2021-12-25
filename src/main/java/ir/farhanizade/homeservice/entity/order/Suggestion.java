package ir.farhanizade.homeservice.entity.order;

import ir.farhanizade.homeservice.entity.core.BaseEntity;
import ir.farhanizade.homeservice.entity.user.Expert;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public class Suggestion extends BaseEntity {
    @ManyToOne
    private Order order;

    @ManyToOne
    private Expert expert;

    private Date dateTime;
    private BigDecimal price;

    private Double duration;
    private Date startDateTime;

    @Builder.Default
    private SuggestionStatus status = SuggestionStatus.PENDING;
}
