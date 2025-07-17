package telran.java58.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import telran.java58.accounting.model.Role;
import telran.java58.post.dao.PostRepository;
import telran.java58.post.model.Post;
import telran.java58.security.model.User;

import java.io.IOException;

@Component
@Order(50)
@RequiredArgsConstructor
public class UpdatePostFilter implements Filter {
    private final PostRepository repository;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (checkEndPoint(request.getMethod(), request.getServletPath())) {
            User user = (User) request.getUserPrincipal();
            String[] parts = request.getServletPath().split("/");
            String postId = parts[parts.length - 1];
            Post post = repository.findById(postId).orElse(null);
            if (post == null || !(user.getName().equalsIgnoreCase(post.getAuthor()))) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean checkEndPoint(String method, String servletPath) {
        return HttpMethod.PATCH.matches(method) && servletPath.matches("/forum/post/\\w+");
    }
}
