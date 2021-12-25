package ir.farhanizade.homeservice.entity.order.message;

import ir.farhanizade.homeservice.entity.user.Expert;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public class Suggestion extends BaseMessage<Expert> {

    private Double duration;

    @Builder.Default
    private SuggestionStatus status = SuggestionStatus.PENDING;
}
