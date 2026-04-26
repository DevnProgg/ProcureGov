package com.ProcureGov.controller.auth;

import com.ProcureGov.model.EmployeeData;
import com.ProcureGov.model.SupplierData;
import com.ProcureGov.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/auth/login")
public class LoginController extends HttpServlet {
    //Dependency Injection
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp")
                .forward(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            Object loginResult = authService.Login(email, password);

            if (loginResult == null) {
                request.setAttribute("loginError", "Invalid email or password.");
                request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
                return;
            }

            request.getSession().setAttribute("user", loginResult);

            if (loginResult instanceof EmployeeData) {
                response.sendRedirect(request.getContextPath() + "/app/officer/dashboard");
            } else if (loginResult instanceof SupplierData) {
                response.sendRedirect(request.getContextPath() + "/app/supplier/dashboard");
            }
        } catch (Exception e) {
            throw new ServletException("Login error", e);
        }
    }
}
