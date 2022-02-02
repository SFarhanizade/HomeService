package ir.farhanizade.homeservice.entity.order;

import ir.farhanizade.homeservice.entity.MyTransaction;
import ir.farhanizade.homeservice.entity.core.BaseEntity;
import ir.farhanizade.homeservice.entity.order.message.Request;
import ir.farhanizade.homeservice.entity.order.message.Suggestion;
import ir.farhanizade.homeservice.entity.service.SubService;
import ir.farhanizade.homeservice.exception.DuplicateEntityException;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.*;

//@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class MyOrder extends BaseEntity {

    @ManyToOne(cascade = CascadeType.ALL)
    private SubService service;

    private Date finishDateTime;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "myOrder")
    private Request request;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "myOrder", fetch = FetchType.EAGER)
    private Set<Suggestion> suggestions;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.WAITING_FOR_SUGGESTION;

    @OneToOne(mappedBy = "myOrder")
    private MyTransaction myTransaction;

    @OneToOne(mappedBy = "myOrder")
    private MyComment myComment;

    public void suggest(Suggestion suggestion) throws DuplicateEntityException {
        if (suggestion == null)
            throw new IllegalArgumentException("Null Suggestion!");
        if (suggestions == null)
            suggestions = new HashSet<>();
        boolean exists = !suggestions.add(suggestion);
        if (exists) throw new DuplicateEntityException("You Can't Suggest On This Order More Than Once!");
        status = OrderStatus.WAITING_FOR_SELECTION;
    }


    public void addRequest(Request request) {
        this.request = request;
    }
}
