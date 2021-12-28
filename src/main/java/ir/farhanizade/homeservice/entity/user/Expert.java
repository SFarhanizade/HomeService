package ir.farhanizade.homeservice.entity.user;

import ir.farhanizade.homeservice.entity.core.BasePerson;
import ir.farhanizade.homeservice.entity.order.Comment;
import ir.farhanizade.homeservice.entity.service.SubService;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToMany(cascade = CascadeType.ALL)
    @Builder.Default
    private List<SubService> expertises = new ArrayList<>();

    @Builder.Default
    private Integer points = 0;

    public void addPoints(Integer points) {
        this.points += points;
    }
}
