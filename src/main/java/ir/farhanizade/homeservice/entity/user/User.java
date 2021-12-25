package ir.farhanizade.homeservice.entity.user;

import ir.farhanizade.homeservice.entity.Transaction;
import ir.farhanizade.homeservice.entity.core.BasePerson;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@Entity
public class User extends BasePerson {
    private Date dateTime;
    private BigDecimal credit;
    @Builder.Default
    private UserStatus status = UserStatus.NEW;
    @OneToMany
    private List<Transaction> transactions;

    public void addTransaction(Transaction transaction){
        if(transaction==null)
            throw new IllegalStateException("Null Transaction!");
        BigDecimal amount = transaction.getAmount();
        if(this.getId()==transaction.getPayer().getId())
            amount.multiply(new BigDecimal(-1));
        credit.add(amount);
    }
}
