package ir.farhanizade.homeservice.entity.order.message;

import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.entity.user.User;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

//@EqualsAndHashCode(callSuper = true)
//@Data
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(
        uniqueConstraints = {@UniqueConstraint(columnNames = {"order_id", "owner_id"})}
)
public class Suggestion extends BaseMessage {

    @ManyToOne
    private Expert owner;

    @Column(nullable = false)
    private Double duration;

    @Builder.Default
    @Column(nullable = false)
    private SuggestionStatus suggestionStatus = SuggestionStatus.PENDING;
}
