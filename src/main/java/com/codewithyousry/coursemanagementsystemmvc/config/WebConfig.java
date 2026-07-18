package com.codewithyousry.coursemanagementsystemmvc.config;

import com.codewithyousry.coursemanagementsystemmvc.exception.GlobalExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/dashboard");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse response,
                                  Object handler, org.springframework.web.servlet.ModelAndView modelAndView) {
                if (modelAndView == null) {
                    return;
                }
                HttpSession session = request.getSession(false);
                if (session == null) {
                    return;
                }
                String viewName = modelAndView.getViewName();
                if (viewName != null && viewName.startsWith("redirect:")) {
                    return;
                }
                Object error = session.getAttribute(GlobalExceptionHandler.FLASH_ERROR_KEY);
                if (error != null) {
                    modelAndView.addObject("errorMessage", error);
                    session.removeAttribute(GlobalExceptionHandler.FLASH_ERROR_KEY);
                }
            }
        });
    }
}
