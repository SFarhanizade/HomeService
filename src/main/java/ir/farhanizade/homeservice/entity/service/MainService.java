package ir.farhanizade.homeservice.entity.service;

import ir.farhanizade.homeservice.entity.core.BaseService;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
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
public class MainService extends BaseService {
    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Builder.Default
    private List<SubService> subServices = new ArrayList<>();;

    public void addSubService(SubService service) {
        if(subServices==null)
            subServices=new ArrayList<>();
        subServices.add(service);
    }
}
