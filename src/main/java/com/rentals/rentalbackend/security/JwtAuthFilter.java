package com.rentals.rentalbackend.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.io.IOException;

@Component @RequiredArgsConstructor
public class JwtAuthFilter extends org.springframework.web.filter.OncePerRequestFilter {
    private final JwtService jwt;
    private final CustomUserDetailsService uds;

    @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String header = req.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                var claims = jwt.parse(token).getBody();
                String email = claims.getSubject();
                UserDetails d = uds.loadUserByUsername(email);
                var auth = new UsernamePasswordAuthenticationToken(d, null, d.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception ignored) { /* invalid token: continue unauthenticated */ }
        }
        chain.doFilter(req, res);
    }
}

