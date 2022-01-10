package ir.farhanizade.homeservice.entity.order;

import ir.farhanizade.homeservice.entity.Transaction;
import ir.farhanizade.homeservice.entity.core.BaseEntity;
import ir.farhanizade.homeservice.entity.order.message.BaseMessageStatus;
import ir.farhanizade.homeservice.entity.order.message.Request;
import ir.farhanizade.homeservice.entity.order.message.Suggestion;
import ir.farhanizade.homeservice.entity.order.message.SuggestionStatus;
import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.exception.DuplicateEntityException;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.*;

//@EqualsAndHashCode(callSuper = true)
//@Data
@Entity
@Table(name = "MyOrder")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Order extends BaseEntity {

    @ManyToOne(cascade = CascadeType.ALL)
    private SubService service;

    private Date finishDateTime;

    @OneToOne(cascade = CascadeType.ALL)
    private Request request;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Suggestion> suggestions;

    @OneToOne(cascade = CascadeType.ALL)
    private Suggestion suggestion;

    @Builder.Default
    private OrderStatus status = OrderStatus.WAITING_FOR_SUGGESTION;

    @OneToOne
    private Transaction transaction;

    @OneToOne
    private Comment comment;

    public void suggest(Suggestion suggestion) throws DuplicateEntityException {
        if (suggestion == null)
            throw new IllegalArgumentException("Null Suggestion!");
        if (suggestions == null)
            suggestions = new HashSet<>();
        boolean exists = !suggestions.add(suggestion);
        if(exists) throw new DuplicateEntityException("You Can't Suggest On This Order More Than Once!");
        status = OrderStatus.WAITING_FOR_SELECTION;
    }

    public void acceptSuggestion(Suggestion suggestion) {
        this.suggestion = suggestion;
        suggestion.setStatus(BaseMessageStatus.BUSY);
        suggestion.setSuggestionStatus(SuggestionStatus.ACCEPTED);
        suggestions.stream()
                .filter(s -> !s.equals(suggestion))
                .forEach(s -> s.setSuggestionStatus(SuggestionStatus.REJECTED));
        request.setStatus(BaseMessageStatus.BUSY);
        status = OrderStatus.WAITING_FOR_EXPERT;
    }

    public void addRequest(Request request) {
        this.request = request;
    }
}
