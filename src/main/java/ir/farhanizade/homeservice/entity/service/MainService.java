package ir.farhanizade.homeservice.entity.service;

import ir.farhanizade.homeservice.entity.core.BaseService;
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
public class MainService extends BaseService {
    @OneToMany
    private List<SubService> subServices;
}
