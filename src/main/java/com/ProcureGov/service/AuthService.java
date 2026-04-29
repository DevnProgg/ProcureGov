package com.ProcureGov.service;

import com.ProcureGov.dto.LoginResult;
import com.ProcureGov.model.Account;
import com.ProcureGov.model.Supplier;
import com.ProcureGov.model.User;
import com.ProcureGov.repository.*;

import java.sql.Connection;

public class AuthService {
    //Dependency Injection
    private final AccountRepository accountRepository = new AccountRepository();
    private final UserRepository userRepository = new UserRepository();
    private final SupplierRepository supplierRepository = new SupplierRepository();
    private final EmployeeDataRepository employeeDataRepository = new EmployeeDataRepository();
    private final SupplierDataRepository supplierDataRepository = new SupplierDataRepository();
    private final RoleRepository roleRepository = new RoleRepository();

    /*
    Supplier -> user -> account
     */
    public void RegisterSupplierAccount(Account acc, Supplier supplier) throws Exception {
        User user = new User();
        acc.setRole_id(roleRepository.getSupplierRoleID());

        try (Connection conn = accountRepository.getConnection()) {
            conn.setAutoCommit(false);

            try {
                Supplier s = supplierRepository.save(conn, supplier);
                if (s.getSupplier_id() == 0) throw new Exception("Failed To create supplier");

                user.setSupplier_id(s.getSupplier_id());
                User u = userRepository.create(conn, user);
                if (u.getUser_id() == 0) throw new Exception("Failed To create user");

                acc.setUser_id(u.getUser_id());
                Account account = accountRepository.createAccount(conn, acc);
                if (account.getAccount_id() == 0) throw new Exception("Failed To create account");

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public Object Login(String username, String password) throws Exception {
        LoginResult result = accountRepository.loginToAccount(username, password);

        if (result == null) return null;

        String role = result.roleName();
        int userId = result.userId();

        if ("PROCUREMENT_OFFICER".equalsIgnoreCase(role) || "BOARD_MEMBER".equalsIgnoreCase(role)) {
            return employeeDataRepository.getEmployeeDataByID(userId);
        } else if ("SUPPLIER".equalsIgnoreCase(role)) {
            return supplierDataRepository.findByUserId(userId);
        }
        return null;
    }
}
