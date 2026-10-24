package com.ledgerflow.beneficiary;

import com.ledgerflow.beneficiary.dto.BeneficiaryResponse;
import com.ledgerflow.beneficiary.dto.CreateBeneficiaryRequest;
import com.ledgerflow.user.User;
import com.ledgerflow.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/beneficiaries")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;
    private final UserRepository userRepository;

    public BeneficiaryController(
            BeneficiaryService beneficiaryService,
            UserRepository userRepository
    ) {
        this.beneficiaryService = beneficiaryService;
        this.userRepository = userRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BeneficiaryResponse createBeneficiary(
            @Valid @RequestBody CreateBeneficiaryRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        return beneficiaryService.createBeneficiary(user, request);
    }

    @GetMapping
    public java.util.List<BeneficiaryResponse> getBeneficiaries(
            Authentication authentication
    ) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        return beneficiaryService.getBeneficiaries(user);
    }
}
