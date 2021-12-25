package ir.farhanizade.homeservice.entity.order.message;

import ir.farhanizade.homeservice.entity.user.Expert;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public class Suggestion extends BaseMessage<Expert> {

    @Column(nullable = false)
    private Double duration;

    @Builder.Default
    @Column(nullable = false)
    private SuggestionStatus status = SuggestionStatus.PENDING;
}
