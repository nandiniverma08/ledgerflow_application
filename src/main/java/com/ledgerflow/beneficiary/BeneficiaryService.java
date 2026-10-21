package com.ledgerflow.beneficiary;

import com.ledgerflow.beneficiary.dto.BeneficiaryResponse;
import com.ledgerflow.beneficiary.dto.CreateBeneficiaryRequest;
import com.ledgerflow.user.User;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;

    public BeneficiaryService(
            BeneficiaryRepository beneficiaryRepository
    ) {
        this.beneficiaryRepository = beneficiaryRepository;
    }

    @Transactional
    public BeneficiaryResponse createBeneficiary(
            User user,
            CreateBeneficiaryRequest request
    ) {
        boolean alreadyExists =
                beneficiaryRepository.existsByUserAndAccountNumber(
                        user,
                        request.accountNumber()
                );

        if (alreadyExists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Beneficiary already exists"
            );
        }

        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setUser(user);
        beneficiary.setBeneficiaryName(request.beneficiaryName());
        beneficiary.setAccountNumber(request.accountNumber());
        beneficiary.setBankName(request.bankName());
        beneficiary.setStatus("ACTIVE");

        Beneficiary saved =
                beneficiaryRepository.save(beneficiary);

        return new BeneficiaryResponse(
                saved.getId(),
                saved.getBeneficiaryName(),
                saved.getAccountNumber(),
                saved.getBankName(),
                saved.getStatus(),
                saved.getCreatedAt()
        );
    }


}
