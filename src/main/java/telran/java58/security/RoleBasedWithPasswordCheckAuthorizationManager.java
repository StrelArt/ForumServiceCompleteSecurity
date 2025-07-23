package telran.java58.security;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.function.Supplier;

public class RoleBasedWithPasswordCheckAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final AuthorizationManager<RequestAuthorizationContext> roleCheck;
    private final PasswordExpiryAuthorizationManager passwordExpiryManager;

    public RoleBasedWithPasswordCheckAuthorizationManager(
            AuthorizationManager<RequestAuthorizationContext> roleCheck,
            PasswordExpiryAuthorizationManager passwordExpiryManager
    ) {
        this.roleCheck = roleCheck;
        this.passwordExpiryManager = passwordExpiryManager;
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authSupplier, RequestAuthorizationContext context) {
        AuthorizationDecision passwordCheck = passwordExpiryManager.check(authSupplier, context);
        AuthorizationDecision roleCheckDecision = roleCheck.check(authSupplier, context);
        return new AuthorizationDecision(passwordCheck.isGranted() && roleCheckDecision.isGranted());
    }
}
