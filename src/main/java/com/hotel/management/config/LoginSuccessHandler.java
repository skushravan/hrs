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
 * Authentication success handler that redirects users to different dashboards
 * based on their role after successful login.
 * 
 * Role-based redirects:
 * - ADMIN → /admin/dashboard
 * - STAFF → /staff/dashboard
 * - CUSTOMER (ROLE_USER) → /customer/dashboard
 */
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                      HttpServletResponse response, 
                                      Authentication authentication) throws IOException, ServletException {
        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String redirectUrl = determineRedirectUrl(authorities);
        
        response.sendRedirect(request.getContextPath() + redirectUrl);
    }

    /**
     * Determines the redirect URL based on user roles
     * Priority: ADMIN > STAFF > CUSTOMER
     * 
     * @param authorities the collection of user authorities
     * @return the redirect URL path
     */
    private String determineRedirectUrl(Collection<? extends GrantedAuthority> authorities) {
        // Check for ADMIN role (highest priority)
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            if (role.equals("ROLE_ADMIN")) {
                return "/admin/dashboard";
            }
        }
        
        // Check for STAFF role (includes ROLE_STAFF, ROLE_MANAGER, ROLE_RECEPTIONIST)
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            if (role.equals("ROLE_STAFF") || role.equals("ROLE_MANAGER") || role.equals("ROLE_RECEPTIONIST")) {
                return "/staff/dashboard";
            }
        }
        
        // Check for CUSTOMER role (ROLE_USER)
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            if (role.equals("ROLE_USER")) {
                return "/customer/dashboard";
            }
        }
        
        // Default redirect if no matching role found
        return "/";
    }
}

