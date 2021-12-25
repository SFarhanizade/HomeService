package ir.farhanizade.homeservice.entity.user;

import ir.farhanizade.homeservice.entity.core.BasePerson;
import ir.farhanizade.homeservice.entity.service.SubService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public class Expert extends BasePerson {
    private String picURL;

    @OneToMany
    private List<SubService> expertise;


}
