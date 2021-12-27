package ir.farhanizade.homeservice.entity.user;

import ir.farhanizade.homeservice.entity.Transaction;
import ir.farhanizade.homeservice.entity.core.BasePerson;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

//@EqualsAndHashCode(callSuper = true)
//@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class User extends BasePerson {
    @Column(nullable = false)
    @Builder.Default
    private Date dateTime = new Date(System.currentTimeMillis());

    private BigDecimal credit;

    @Builder.Default
    @Column(nullable = false)
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
