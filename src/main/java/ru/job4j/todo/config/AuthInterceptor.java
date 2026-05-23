package ru.job4j.todo.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Set;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final String USER_SESSION_ATTRIBUTE = "user";
    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/login",
            "/register",
            "/error",
            "/favicon.svg"
    );

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        if (isPublic(request)) {
            return true;
        }
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(USER_SESSION_ATTRIBUTE) != null) {
            return true;
        }
        response.sendRedirect(request.getContextPath() + "/login");
        return false;
    }

    private boolean isPublic(HttpServletRequest request) {
        String path = normalizePath(request);
        return PUBLIC_PATHS.contains(path)
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.startsWith("/webjars/");
    }

    private String normalizePath(HttpServletRequest request) {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (!contextPath.isEmpty() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }
        int pathParameterIndex = path.indexOf(';');
        if (pathParameterIndex >= 0) {
            path = path.substring(0, pathParameterIndex);
        }
        return path;
    }
}
