package com.ProcureGov.service;

import com.ProcureGov.dto.LoginResult;
import com.ProcureGov.model.Account;
import com.ProcureGov.model.Employee;
import com.ProcureGov.model.Supplier;
import com.ProcureGov.model.User;
import com.ProcureGov.repository.*;

public class AuthService {
    //Dependency Injection
    private final AccountRepository accountRepository = new AccountRepository();
    private final UserRepository userRepository = new UserRepository();
    private final EmployeeRepository employeeRepository = new EmployeeRepository();
    private final SupplierRepository supplierRepository = new SupplierRepository();
    private final EmployeeDataRepository employeeDataRepository = new EmployeeDataRepository();
    private final SupplierDataRepository supplierDataRepository = new SupplierDataRepository();
    private final RoleRepository roleRepository = new RoleRepository();

    /*
    Employee -> user -> account
     */
    void CreateOfficerAccount() throws Exception{
        User user = new User();
        Employee emp = new Employee();
        emp.setFull_names("Lauren Khalapa Qokolo");
        emp.setPhone_number("+266 5327 8674");
        emp.setGender("Male");
        Account acc = new Account();
        acc.setUsername("lauren@procurement.gov.ls");
        acc.setPassword_hash("lauren@123");

        Employee e = employeeRepository.create(emp);
        user.setEmployee_id(e.getEmployee_id());
        userRepository.create(user);
        acc.setUser_id(user.getUser_id());
        accountRepository.createAccount(acc);
    }
    /*
    Supplier -> user -> account
     */
    public void RegisterSupplierAccount(Account acc, Supplier supplier) throws Exception {
        User user = new User();

        Supplier s = supplierRepository.save(supplier);
        if(s.getSupplier_id() == 0) throw new Exception("Failed To create supplier");
        user.setSupplier_id(s.getSupplier_id());
        User u = userRepository.create(user);
        if(u.getUser_id() == 0) throw new Exception("Failed To create supplier");
        acc.setUser_id(u.getUser_id());
        acc.setRole_id(roleRepository.getSupplierRoleID());
        Account account = accountRepository.createAccount(acc);
        if(account.getAccount_id() == 0) throw new Exception("Failed To create supplier");
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
