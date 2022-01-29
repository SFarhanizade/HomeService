package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.controller.BankController;
import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.dto.out.TransactionOutDto;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.Transaction;
import ir.farhanizade.homeservice.entity.user.Customer;
import ir.farhanizade.homeservice.entity.user.Expert;
import ir.farhanizade.homeservice.exception.BadEntryException;
import ir.farhanizade.homeservice.exception.EntityNotFoundException;
import ir.farhanizade.homeservice.exception.NotEnoughMoneyException;
import ir.farhanizade.homeservice.exception.UserNotLoggedInException;
import ir.farhanizade.homeservice.repository.TransactionRepository;
import ir.farhanizade.homeservice.repository.user.CustomerRepository;
import ir.farhanizade.homeservice.repository.user.ExpertRepository;
import ir.farhanizade.homeservice.security.user.LoggedInUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository repository;
    private final BankController bankController;
    private final CustomerRepository customerRepository;
    private final ExpertRepository expertRepository;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public EntityOutDto save(Transaction transaction, String method) throws NotEnoughMoneyException {
        if (transaction == null)
            throw new IllegalStateException("Null Transaction!");
        Customer payer = transaction.getPayer();
        Expert recipient = transaction.getRecipient();
        BigDecimal amount = transaction.getAmount();
        BigDecimal recipientCredit = recipient.getCredit();
        BigDecimal payerCredit = payer.getCredit();
        if ("credit".equals(method)) {
            if (payerCredit.compareTo(amount) == -1)
                throw new NotEnoughMoneyException("");
            payer.setCredit(payerCredit.subtract(amount));
        } else {
            if (!bankController.pay())
                throw new NotEnoughMoneyException("");
        }
        recipient.setCredit(recipientCredit.add(amount.multiply(new BigDecimal(0.7))));
        Transaction saved = repository.save(transaction);
        return new EntityOutDto(saved.getId());
    }

    public CustomPage<TransactionOutDto> findByUserId(Pageable pageable) throws UserNotLoggedInException, BadEntryException, EntityNotFoundException {
        Long id = LoggedInUser.id();
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
                .customerName(transaction.getPayer().getName())
                .expertId(transaction.getRecipient().getId())
                .expertName(transaction.getRecipient().getName())
                .amount(transaction.getAmount())
                .dateTime(transaction.getCreatedTime())
                .build();
    }

    public TransactionOutDto findById(Long transaction) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException {
        Long id = LoggedInUser.id();
        Optional<Transaction> byId = repository.findByIdAndOwnerId(transaction, id);
        Transaction result = byId.orElseThrow(() -> new EntityNotFoundException("Transaction Not Found!"));
        return convert2Dto(result);
    }
}
