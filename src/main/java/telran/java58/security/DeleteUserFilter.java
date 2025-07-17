package telran.java58.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import telran.java58.accounting.dao.UserAccountRepository;
import telran.java58.accounting.model.Role;
import telran.java58.accounting.model.UserAccount;
import telran.java58.security.model.User;

import java.io.IOException;

@Component
@Order(40)
public class DeleteUserFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (checkEndPoint(request.getMethod(), request.getServletPath())) {
            User user = (User) request.getUserPrincipal();
            String[] parts = request.getServletPath().split("/");
            String owner = parts[parts.length - 1];
            if (!(user.getRoles().contains(Role.ADMINISTRATOR.name()) || user.getName().equalsIgnoreCase(owner))) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean checkEndPoint(String method, String servletPath) {
        return HttpMethod.DELETE.matches(method) && servletPath.matches("/account/user/\\w+");
    }
}
