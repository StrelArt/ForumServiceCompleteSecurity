package telran.java58.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(30)
public class UpdateByOwnerFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (checkEndPoint(request.getMethod(), request.getServletPath())) {
            String login = request.getUserPrincipal().getName();
            String[] parts = request.getServletPath().split("/");
            String owner = parts[parts.length - 1];
            if (!login.equalsIgnoreCase(owner)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean checkEndPoint(String method, String servletPath) {
        return (
                HttpMethod.PATCH.matches(method) && servletPath.matches("/account/user/\\w+")
                || HttpMethod.POST.matches(method) && servletPath.matches("/forum/post/\\w+")
                || HttpMethod.PATCH.matches(method) && servletPath.matches("/forum/post/\\w+/comment/\\w+")

        );
    }
}
