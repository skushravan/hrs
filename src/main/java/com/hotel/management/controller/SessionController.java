package com.hotel.management.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * Controller for exposing basic session details and handling invalid session redirects.
 */
@Controller
@RequestMapping("/session")
public class SessionController {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss");

    @GetMapping("/info")
    public String sessionInfo(Authentication authentication, HttpSession httpSession, Model model) {
        boolean authenticated = authentication != null && authentication.isAuthenticated();

        model.addAttribute("authenticated", authenticated);
        model.addAttribute("username", authenticated ? authentication.getName() : "Guest");
        model.addAttribute("roles", authenticated
                ? authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList())
                : null);

        if (httpSession != null) {
            model.addAttribute("sessionId", httpSession.getId());
            model.addAttribute("creationTime", formatEpochMillis(httpSession.getCreationTime()));
            model.addAttribute("lastAccessedTime", formatEpochMillis(httpSession.getLastAccessedTime()));
        }

        return "session-info";
    }

    @GetMapping("/invalid")
    public String invalidSession(Model model) {
        model.addAttribute("authenticated", false);
        model.addAttribute("sessionInvalid", true);
        model.addAttribute("errorMessage", "Your session is no longer valid. Please log in again.");
        return "session-info";
    }

    private String formatEpochMillis(long epochMillis) {
        return Instant.ofEpochMilli(epochMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .format(FORMATTER);
    }
}

