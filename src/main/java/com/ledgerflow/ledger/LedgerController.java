package com.ledgerflow.ledger;

import com.ledgerflow.ledger.dto.AccountLedgerSummaryResponse;
import com.ledgerflow.ledger.dto.AccountStatementResponse;
import com.ledgerflow.ledger.dto.LedgerEntryResponse;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("api/v1/accounts")
public class LedgerController {

    private final LedgerService ledgerService;

    public LedgerController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @GetMapping("/{accountNumber}/ledger")
    public Page<LedgerEntryResponse> getAccountLedger(
            @AuthenticationPrincipal String email,
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate
    ) {
        return ledgerService.getLedgerEntriesByAccount(
                email, accountNumber, page, size, fromDate, toDate
        );
    }

    @GetMapping("/{accountNumber}/ledger/summary")
    public AccountLedgerSummaryResponse getAccountLedgerSummary(
            @AuthenticationPrincipal String email,
            @PathVariable String accountNumber
    ) {
        return ledgerService.fetchAccountLedgerSummary(
                email,
                accountNumber
        );
    }

    @GetMapping("/{accountNumber}/statement")
    public AccountStatementResponse getAccountStatement(
            @AuthenticationPrincipal String email,
            @PathVariable String accountNumber
    ) {
        return ledgerService.getAccountStatement(email, accountNumber);
    }
}
