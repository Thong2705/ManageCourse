package com.mockproject.group3.security;

import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

@Component
public class OAuthAuthenicationSuccessHandler implements AuthenticationSuccessHandler{
    Logger logger = LoggerFactory.getLogger(OAuthAuthenicationSuccessHandler.class);
    public void onAuthenticationSuccess (HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException{

        logger.info("OAuthAuthenicationSuccessHandler");
        DefaultOAuth2User user = (DefaultOAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = user.getAttributes();

        // Lưu thông tin người dùng vào session
        request.getSession().setAttribute("userAttributes", attributes);

        new DefaultRedirectStrategy().sendRedirect(request, response, "/users/register");

    }

}
