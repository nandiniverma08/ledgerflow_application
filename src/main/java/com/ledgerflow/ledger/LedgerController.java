package com.ledgerflow.ledger;

import com.ledgerflow.ledger.dto.LedgerEntryResponse;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam(defaultValue = "10") int size
    ) {
        return ledgerService.getLedgerEntriesByAccount(
                email,
                accountNumber,
                page,
                size
        );
    }
}
