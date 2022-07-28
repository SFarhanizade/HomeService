package ir.farhanizade.homeservice.entity.user;

import ir.farhanizade.homeservice.entity.service.MyService;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

//@EqualsAndHashCode(callSuper = true)
//@Data
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserExpert extends MyUser {
    private String picURL;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private Set<MyService> expertises = new HashSet<>();

    @Builder.Default
    private Integer points = 0;

    public void addPoints(Integer points) {
        this.points += points;
    }

    public boolean addService(MyService service) {
        return expertises.add(service);
    }
}
