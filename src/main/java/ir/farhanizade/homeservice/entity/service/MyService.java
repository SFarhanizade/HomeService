package ir.farhanizade.homeservice.entity.service;

import ir.farhanizade.homeservice.entity.core.BaseService;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class MyService extends BaseService {
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private MyService parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Builder.Default
    private List<MyService> subMyServices = new ArrayList<>();

    @Column(nullable = false)
    private BigDecimal basePrice;
    private String description;
}
