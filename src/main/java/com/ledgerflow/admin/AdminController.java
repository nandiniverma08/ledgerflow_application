package com.ledgerflow.admin;

import com.ledgerflow.admin.dto.AdminAccountResponse;
import com.ledgerflow.admin.dto.AdminUserResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
