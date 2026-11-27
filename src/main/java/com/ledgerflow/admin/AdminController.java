package com.ledgerflow.admin;

import com.ledgerflow.admin.dto.AdminAccountResponse;
import com.ledgerflow.admin.dto.AdminUserResponse;
import com.ledgerflow.admin.dto.FreezeAccountRequest;
import com.ledgerflow.admin.dto.FreezeAccountResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public List<AdminUserResponse> getAllUsers() {
        return adminService.getAllUsers();
    }

    @GetMapping("/accounts")
    public List<AdminAccountResponse> getAllAccounts() {
        return adminService.getAllAccounts();
    }

    @PatchMapping("/accounts/{accountNumber}/freeze")
    public FreezeAccountResponse freezeAccount(
            @PathVariable String accountNumber,
            @Valid @RequestBody FreezeAccountRequest request,
            Authentication authentication) {

        return adminService.freezeByAccountNumber(
                accountNumber,
                request.reason(),
                authentication.getName()
        );
    }
}
