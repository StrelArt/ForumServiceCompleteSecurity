package telran.java58.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.core.Authentication;
import telran.java58.accounting.dao.UserAccountRepository;
import telran.java58.accounting.dto.exception.UserNotFoundException;
import telran.java58.accounting.model.UserAccount;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class PasswordExpiryAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final UserAccountRepository repository;
    private final long expiryDays;

    private final Set<String> allowedEndpoints = Set.of(
            "POST /account/register",
            "PATCH /account/password",
            "GET /forum/posts"
    );

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier,
                                       RequestAuthorizationContext context) {

        String method = context.getRequest().getMethod();
        String uri = context.getRequest().getRequestURI();

        boolean isAllowed = allowedEndpoints.stream().anyMatch(endpoint -> {
            String[] parts = endpoint.split(" ");
            String allowedMethod = parts[0];
            String allowedPath = parts[1];
            return method.equalsIgnoreCase(allowedMethod) && uri.startsWith(allowedPath);
        });

        if (isAllowed) return new AuthorizationDecision(true);

        Authentication authentication = authenticationSupplier.get();
        if (authentication == null || !authentication.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }

        String login = authentication.getName();
        UserAccount user = repository.findById(login).orElseThrow(UserNotFoundException::new);

        LocalDateTime passwordDate = user.getPasswordChangeDate();
        boolean expired = passwordDate == null ||
                passwordDate.isBefore(LocalDateTime.now().minusDays(expiryDays));

        return new AuthorizationDecision(!expired);
    }
}
