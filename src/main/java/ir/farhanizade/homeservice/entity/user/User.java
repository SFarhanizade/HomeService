package ir.farhanizade.homeservice.entity.user;

import ir.farhanizade.homeservice.entity.Transaction;
import ir.farhanizade.homeservice.entity.core.BasePerson;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
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

    @ManyToMany
    private List<UserType> roles;

    @Column(nullable = false)
    @Builder.Default
    private Date dateTime = new Date(System.currentTimeMillis());

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal credit = new BigDecimal(0);

    @Builder.Default
    @Column(nullable = false)
    private UserStatus status = UserStatus.NEW;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction transaction){
        if(transaction==null)
            throw new IllegalStateException("Null Transaction!");
        transactions.add(transaction);

    }
}
