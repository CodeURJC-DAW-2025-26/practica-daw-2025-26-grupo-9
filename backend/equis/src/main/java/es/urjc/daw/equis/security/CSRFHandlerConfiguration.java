package es.urjc.daw.equis.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class CSRFHandlerConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CSRFHandlerInterceptor());
    }

    static class CSRFHandlerInterceptor implements HandlerInterceptor {

        @Override
        public void postHandle(
                HttpServletRequest request,
                HttpServletResponse response,
                Object handler,
                ModelAndView modelAndView) throws Exception {

            if (modelAndView == null) return;

            CsrfToken csrf = (CsrfToken) request.getAttribute("_csrf");

            if (csrf != null) {
                modelAndView.addObject("csrfToken", csrf.getToken());
                modelAndView.addObject("csrfParameterName", csrf.getParameterName());
            }
        }
    }
}