package com.ProcureGov.controller.auth;

import com.ProcureGov.model.Account;
import com.ProcureGov.model.Supplier;
import com.ProcureGov.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/auth/register")
public class RegisterController extends HttpServlet {
    //Dependency injection
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp")
                .forward(request,response);
    }

    @Override
    protected  void doPost(HttpServletRequest request, HttpServletResponse response)
        throws  ServletException, IOException{
        //parse form objects
        String businessName = request.getParameter("businessName");
        String regNumber = request.getParameter("reg_number");
        String address = request.getParameter("address");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phone_number");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        //create objects
        Account acc = new Account();
        acc.setUsername(username);
        acc.setPassword_hash(password);
        acc.setActive_status(true);

        Supplier supplier = new Supplier();
        supplier.setBusiness_name(businessName);
        supplier.setEmail(email);
        supplier.setAddress(address);
        supplier.setPhone_number(phoneNumber);
        supplier.setReg_number(regNumber);

        try {
            authService.RegisterSupplierAccount(acc, supplier);
            response.sendRedirect(request.getContextPath() + "/auth/login");
        } catch (Exception e) {
            throw new ServletException("Register error", e);
        }
    }
}
