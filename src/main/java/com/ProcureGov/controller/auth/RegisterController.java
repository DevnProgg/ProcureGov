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
import java.util.logging.Level;
import java.util.logging.Logger;
import com.ProcureGov.repository.AccountRepository;

@WebServlet("/auth/register")
public class RegisterController extends HttpServlet {
    //Dependency injection
    private final AuthService authService = new AuthService();
    private final AccountRepository accountRepository = new AccountRepository();
    private static final Logger logger = Logger.getLogger(RegisterController.class.getName());

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
        // Pre-check username availability to avoid opening a DB transaction unnecessarily
        try {
            if (username != null && !username.isEmpty() && accountRepository.existsByUsername(username)) {
                request.setAttribute("registrationError", "An account with that username already exists. Please choose a different username.");
                // Re-populate submitted fields (avoid sending password back)
                request.setAttribute("businessName", businessName);
                request.setAttribute("reg_number", regNumber);
                request.setAttribute("address", address);
                request.setAttribute("email", email);
                request.setAttribute("phone_number", phoneNumber);
                request.setAttribute("username", username);

                request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
                return;
            }
        } catch (Exception e) {
            // If the pre-check fails (DB error), log and continue — the registration call will still handle integrity errors.
            logger.log(Level.WARNING, "Failed to check username availability, proceeding with registration", e);
        }

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
            // Log the full exception server-side to ensure container logs capture the root cause
            logger.log(Level.SEVERE, "Unhandled error during supplier registration", e);

            // Inspect root cause to present a friendly message for common constraint errors (e.g., duplicate username)
            Throwable root = e;
            while (root.getCause() != null) root = root.getCause();

            if (root instanceof java.sql.SQLIntegrityConstraintViolationException
                    || (root.getMessage() != null && root.getMessage().toLowerCase().contains("duplicate"))) {
                // Return user to registration form with a friendly error
                request.setAttribute("registrationError", "An account with that username already exists. Please choose a different username.");
                // Re-populate submitted fields (avoid sending password back)
                request.setAttribute("businessName", businessName);
                request.setAttribute("reg_number", regNumber);
                request.setAttribute("address", address);
                request.setAttribute("email", email);
                request.setAttribute("phone_number", phoneNumber);
                request.setAttribute("username", username);

                request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
                return;
            }

            throw new ServletException("Register error", e);
        }
    }
}
