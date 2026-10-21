package com.ledgerflow.beneficiary;

import com.ledgerflow.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {

    List<Beneficiary> findByUser(User user);

    boolean existsByUserAndAccountNumber(
            User user,
            String accountNumber
    );
}
