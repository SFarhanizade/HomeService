package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.entity.Transaction;
import ir.farhanizade.homeservice.entity.order.ServiceOrder;
import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.entity.user.User;
import ir.farhanizade.homeservice.exception.NotEnoughMoneyException;
import ir.farhanizade.homeservice.repository.TransactionRepository;
import ir.farhanizade.homeservice.repository.user.CustomerRepository;
import ir.farhanizade.homeservice.repository.user.ExpertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository repository;
    private final CustomerRepository customerRepository;
    private final ExpertRepository expertRepository;

    @Transactional
    public void save(Transaction transaction) throws NotEnoughMoneyException {
        if (transaction == null)
            throw new IllegalStateException("Null Transaction!");
        ServiceOrder order = transaction.getOrder();
        Customer payer = transaction.getPayer();
        Expert recipient = transaction.getRecipient();
        BigDecimal amount = transaction.getAmount();
        if (payer.getCredit().longValue() < amount.longValue())
            throw new NotEnoughMoneyException("");
        order.setTransaction(transaction);
        payer.setCredit(payer.getCredit().add(amount.multiply(new BigDecimal(-1))));
        recipient.setCredit(recipient.getCredit().add(amount));
        /*payer.addTransaction(transaction);
        recipient.addTransaction(transaction);*/
        repository.save(transaction);
    }
}
