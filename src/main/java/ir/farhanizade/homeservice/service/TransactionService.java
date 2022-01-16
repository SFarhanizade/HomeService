package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.out.TransactionOutDto;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.Transaction;
import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.exception.NotEnoughMoneyException;
import ir.farhanizade.homeservice.repository.TransactionRepository;
import ir.farhanizade.homeservice.repository.user.CustomerRepository;
import ir.farhanizade.homeservice.repository.user.ExpertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository repository;
    private final CustomerRepository customerRepository;
    private final ExpertRepository expertRepository;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
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
        recipient.setCredit(recipientCredit.add(amount.multiply(new BigDecimal(0.7))));
        repository.save(transaction);
    }

    public CustomPage<TransactionOutDto> findByUserId(Long id, Pageable pageable) {
        Page<Transaction> page = repository.findByUserId(id, pageable);
        return convert2Dto(page);
    }

    private CustomPage<TransactionOutDto> convert2Dto(Page<Transaction> page) {
        List<TransactionOutDto> data = page.getContent().stream()
                .map(this::convert2Dto).toList();
        return CustomPage.<TransactionOutDto>builder()
                .data(data)
                .pageNumber(page.getNumber())
                .lastPage(page.getTotalPages())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .build();
    }

    private TransactionOutDto convert2Dto(Transaction transaction) {
        return TransactionOutDto.builder()
                .id(transaction.getId())
                .customerId(transaction.getPayer().getId())
                .customerName(transaction.getPayer().getFName() + " " + transaction.getPayer().getLName())
                .expertId(transaction.getRecipient().getId())
                .expertName(transaction.getRecipient().getFName() + " " + transaction.getRecipient().getLName())
                .amount(transaction.getAmount())
                .dateTime(transaction.getCreatedTime())
                .build();
    }
}
