package com.example.demo.filter;


import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    // Constructor injection instead of field injection
    public JwtRequestFilter(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    //     called for each request. It handles the filtering logic.
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//            throws ServletException, IOException {
//
//        final String authorizationHeader = request.getHeader("Authorization");
//        System.out.println("Authorization Header: " + authorizationHeader); // Log the Authorization header
//
//        String username = null;
//        String jwt = null;
//
//        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//            jwt = authorizationHeader.substring(7);
//            System.out.println(jwt);
//            try {
//                username = jwtUtil.extractUsername(jwt);
//                logger.info("Extracted Username: {}", username); // Log the extracted username
//            } catch (JwtException e) {
//                logger.error("JWT Extraction Error: {}", e.getMessage());  // Log if JWT extraction fails
//            }
//        }
//
//        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//
//            UserDetails userDetails = this.userService.loadUserByEmail(username);
//            System.out.println("UserDetails Loaded: " + userDetails);
//
//            if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
//                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
//                        userDetails, null, userDetails.getAuthorities());
//                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//                logger.info("JWT Token Validated: {}", jwt);  // Log token validation
//            } else {
//                logger.warn("Invalid JWT Token: {}", jwt);  // Log invalid token
//            }
//        } else {
//            System.out.println("No valid authentication found or username is null");
//        }
//        chain.doFilter(request, response);
//    }

    //     called for each request. It handles the filtering logic.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + authorizationHeader); // Log the Authorization header

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            System.out.println(jwt);
            try {
                username = jwtUtil.extractUsername(jwt);
                logger.info("Extracted Username: {}", username); // Log the extracted username
            } catch (JwtException e) {
                logger.error("JWT Extraction Error: {}", e.getMessage());  // Log if JWT extraction fails
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            User userDetails = this.userService.loadUserByUsername(username);
            System.out.println("UserDetails Loaded: " + userDetails);

            if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                logger.info("JWT Token Validated: {}", jwt);  // Log token validation
            } else {
                logger.warn("Invalid JWT Token: {}", jwt);  // Log invalid token
            }
        } else {
            System.out.println("No valid authentication found or username is null");
        }
        chain.doFilter(request, response);
    }

}
