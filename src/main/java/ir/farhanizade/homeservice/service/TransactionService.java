package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.entity.Transaction;
import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.entity.user.Expert;
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

    @Transactional(rollbackFor = Exception.class)
    public void save(Transaction transaction) throws NotEnoughMoneyException {
        if (transaction == null)
            throw new IllegalStateException("Null Transaction!");
        Customer payer = transaction.getPayer();
        Expert recipient = transaction.getRecipient();
        BigDecimal amount = transaction.getAmount();
        BigDecimal payerCredit = payer.getCredit();
        BigDecimal recipientCredit = recipient.getCredit();
        if (payerCredit.compareTo(amount) == -1)
            throw new NotEnoughMoneyException("");
        payer.setCredit(payerCredit.subtract(amount));
        recipient.setCredit(recipientCredit.add(amount));
        repository.save(transaction);
    }
}
