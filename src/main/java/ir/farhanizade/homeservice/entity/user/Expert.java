package ir.farhanizade.homeservice.entity.user;

import ir.farhanizade.homeservice.entity.core.BasePerson;
import ir.farhanizade.homeservice.entity.order.Comment;
import ir.farhanizade.homeservice.entity.service.SubService;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//@EqualsAndHashCode(callSuper = true)
//@Data
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Expert extends User {
    private String picURL;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private Set<SubService> expertises = new HashSet<>();

    @Builder.Default
    private Integer points = 0;

    public void addPoints(Integer points) {
        this.points += points;
    }

    public boolean addService(SubService service) {
        return expertises.add(service);
    }
}
