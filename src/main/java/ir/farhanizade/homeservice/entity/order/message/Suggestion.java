package ir.farhanizade.homeservice.entity.order.message;

import ir.farhanizade.homeservice.entity.order.Order;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.entity.user.User;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

//@EqualsAndHashCode(callSuper = true)
//@Data
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, of = {"owner"})
@Table(
        uniqueConstraints = {@UniqueConstraint(columnNames = {"order_id", "owner_id"})}
)
public class Suggestion extends BaseMessage {


    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(cascade = CascadeType.ALL)
    protected Order order;

    @ManyToOne
    private Expert owner;

    @Column(nullable = false)
    private Double duration;

    @Builder.Default
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SuggestionStatus suggestionStatus = SuggestionStatus.PENDING;
}
