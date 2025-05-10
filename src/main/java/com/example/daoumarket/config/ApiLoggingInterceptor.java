package com.example.daoumarket.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ApiLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        long start = (Long) request.getAttribute("startTime");
        long end = System.currentTimeMillis();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        long duration = end - start;

        System.out.printf("[API] %s %s → %dms%n", method, uri, duration);
    }
}
