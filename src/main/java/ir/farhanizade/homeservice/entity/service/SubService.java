package ir.farhanizade.homeservice.entity.service;

import ir.farhanizade.homeservice.entity.core.BaseService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public class SubService extends BaseService {
    @Column(nullable = false)
    private BigDecimal basePrice;
    private String description;
}
