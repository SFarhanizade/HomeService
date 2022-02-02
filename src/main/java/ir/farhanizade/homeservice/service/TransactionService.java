package ir.farhanizade.homeservice.service;

import ir.farhanizade.homeservice.dto.out.EntityOutDto;
import ir.farhanizade.homeservice.dto.out.TransactionOutDto;
import ir.farhanizade.homeservice.entity.CustomPage;
import ir.farhanizade.homeservice.entity.MyTransaction;
import ir.farhanizade.homeservice.entity.user.UserCustomer;
import ir.farhanizade.homeservice.entity.user.UserExpert;
import ir.farhanizade.homeservice.exception.*;
import ir.farhanizade.homeservice.repository.TransactionRepository;
import ir.farhanizade.homeservice.security.user.LoggedInUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository repository;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public EntityOutDto save(MyTransaction transaction, String method) throws NotEnoughMoneyException, BadEntryException {
        if (transaction == null)
            throw new IllegalStateException("Null Transaction!");
        UserCustomer payer = transaction.getPayer();
        UserExpert recipient = transaction.getRecipient();
        BigDecimal amount = transaction.getAmount();
        BigDecimal recipientCredit = recipient.getCredit();
        BigDecimal payerCredit = payer.getCredit();
        if ("credit".equals(method)) {
            if (payerCredit.compareTo(amount) == -1)
                throw new NotEnoughMoneyException("");
            payer.setCredit(payerCredit.subtract(amount));
        } else {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Boolean> bankResponse;
            try {
                bankResponse = restTemplate.postForEntity("http://localhost:8181/bank/payment",
                        null, Boolean.class);
            } catch (Exception e) {
                throw new BadEntryException("Bank is not available!");
            }
            if (!bankResponse.getBody())
                throw new NotEnoughMoneyException("");
        }
        recipient.setCredit(recipientCredit.add(amount.multiply(new BigDecimal(0.7))));
        MyTransaction saved = repository.save(transaction);
        return new EntityOutDto(saved.getId());
    }

    public CustomPage<TransactionOutDto> findByUserId(Pageable pageable) throws UserNotLoggedInException, BadEntryException, EntityNotFoundException, AccountIsLockedException {
        Long id = LoggedInUser.id();
        Page<MyTransaction> page = repository.findByUserId(id, pageable);
        return convert2Dto(page);
    }

    private CustomPage<TransactionOutDto> convert2Dto(Page<MyTransaction> page) {
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

    private TransactionOutDto convert2Dto(MyTransaction transaction) {
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

    public TransactionOutDto findById(Long transaction) throws EntityNotFoundException, UserNotLoggedInException, BadEntryException, AccountIsLockedException {
        Long id = LoggedInUser.id();
        Optional<MyTransaction> byId = repository.findByIdAndOwnerId(transaction, id);
        MyTransaction result = byId.orElseThrow(() -> new EntityNotFoundException("Transaction Not Found!"));
        return convert2Dto(result);
    }
}
