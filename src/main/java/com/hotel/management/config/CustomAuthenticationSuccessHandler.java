package com.hotel.management.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

/**
 * Custom authentication success handler that redirects users based on their roles
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                      HttpServletResponse response, 
                                      Authentication authentication) throws IOException, ServletException {
        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String redirectUrl = "/"; // Default redirect
        
        // Check user roles and redirect accordingly
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            
            if (role.equals("ROLE_ADMIN")) {
                redirectUrl = "/admin/dashboard";
                break; // Admin has highest priority
            } else if (role.equals("ROLE_MANAGER") || role.equals("ROLE_RECEPTIONIST") || role.equals("ROLE_STAFF")) {
                redirectUrl = "/staff/dashboard";
                // Don't break, continue checking for admin
            } else if (role.equals("ROLE_USER")) {
                redirectUrl = "/customer/dashboard";
                // Don't break, continue checking for higher roles
            }
        }
        
        response.sendRedirect(request.getContextPath() + redirectUrl);
    }
}

