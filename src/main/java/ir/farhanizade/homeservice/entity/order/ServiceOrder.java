package ir.farhanizade.homeservice.entity.order;

import ir.farhanizade.homeservice.entity.core.BaseEntity;
import ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus;
import ir.farhanizade.homeservice.entity.order.message.Request;
import ir.farhanizade.homeservice.entity.order.message.Suggestion;
import ir.farhanizade.homeservice.entity.order.message.SuggestionStatus;
import ir.farhanizade.homeservice.entity.service.SubService;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//@EqualsAndHashCode(callSuper = true)
//@Data
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ServiceOrder extends BaseEntity {

    @ManyToOne(cascade = CascadeType.ALL)
    private SubService service;

    private Date finishDateTime;

    @OneToOne(cascade = CascadeType.ALL)
    private Request request;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Suggestion> suggestions;

    @OneToOne(cascade = CascadeType.ALL)
    private Suggestion suggestion;

    @Builder.Default
    private OrderStatus status = OrderStatus.WAITING_FOR_SUGGESTION;

    public void suggest(Suggestion suggestion) {
        if (suggestion == null)
            throw new IllegalArgumentException("Null Suggestion!");
        if (suggestions == null)
            suggestions = new ArrayList<>();
        suggestions.add(suggestion);
        status = OrderStatus.WAITING_FOR_SELECTION;
    }

    public void acceptSuggestion(Suggestion suggestion) {
        this.suggestion = suggestion;
        suggestions.stream()
                .filter(s -> !s.equals(suggestion))
                .forEach(s -> s.setSuggestionStatus(SuggestionStatus.REJECTED));
        request.setStatus(BaseMessageStatus.BUSY);
        status = OrderStatus.WAITING_FOR_EXPERT;
    }
}
