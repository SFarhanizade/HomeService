package ir.farhanizade.homeservice.entity.order;

import ir.farhanizade.homeservice.entity.core.BaseEntity;
import ir.farhanizade.homeservice.entity.order.message.Request;
import ir.farhanizade.homeservice.entity.order.message.Suggestion;
import ir.farhanizade.homeservice.entity.service.SubService;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public class Order extends BaseEntity {

    @ManyToOne
    private SubService service;

    private Date finishDateTime;

    @OneToOne
    private Request request;

    @OneToMany
    private List<Suggestion> suggestions;

    @OneToOne
    private Suggestion suggestion;

    @Builder.Default
    private OrderStatus status = OrderStatus.WAITING_FOR_SUGGESTION;

    public void suggest(Suggestion suggestion){
        if(suggestion==null)
            throw new IllegalArgumentException("Null Suggestion!");
        if(suggestions==null)
            suggestions = new ArrayList<>();
        suggestions.add(suggestion);
    }
}
