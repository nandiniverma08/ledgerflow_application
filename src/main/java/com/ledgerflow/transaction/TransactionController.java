package com.ledgerflow.transaction;

import com.ledgerflow.transaction.dto.CreateTransferRequest;
import com.ledgerflow.transaction.dto.TransactionResponse;
import com.ledgerflow.user.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransfer(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody CreateTransferRequest request
    ) {
        return transactionService.createTransfer(email, request);
    }

    @GetMapping
    public List<TransactionResponse> getTransactionHistory(
            @AuthenticationPrincipal String email) {

        return transactionService.getTransactionHistory(email);
    }

    @GetMapping("/{referenceNumber}")
    public TransactionResponse getTransactionByReference(
            @AuthenticationPrincipal String email,
            @PathVariable String referenceNumber) {

        return transactionService.getTransactionByReference(
                email, referenceNumber
        );
    }
}
