package ir.farhanizade.homeservice.entity.user;

import ir.farhanizade.homeservice.entity.order.message.Request;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;


@Data
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Customer extends User {


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner", fetch = FetchType.EAGER)
    @Builder.Default
    private List<Request> requests = new ArrayList<>();


    public void addRequest(Request request) {
        requests.add(request);
    }
}
