//package telran.java58.security;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authorization.AuthorizationDecision;
//import org.springframework.security.authorization.AuthorizationManager;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
//import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
//import telran.java58.accounting.dao.UserAccountRepository;
//import telran.java58.accounting.model.Role;
//
//import java.time.LocalDateTime;
//
//@EnableWebSecurity
//@Configuration
//@RequiredArgsConstructor
//public class SecurityConfiguration {
//
//    private final CustomWebSecurity webSecurity;
//    private final UserAccountRepository repository;
//
//    @Value("${security.password.expiry-days}")
//    private long passwordExpiryDays;
//
//    @Bean
//    SecurityFilterChain getSecurityFilterChain(HttpSecurity http) throws Exception {
//        http.httpBasic(Customizer.withDefaults());
//        http.csrf(csrf -> csrf.disable());
//        http.authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/account/register", "/forum/posts/**", "/account/password/change")
//                              .permitAll()
////                .requestMatchers("/account/user/{login}/role/{role}")
////                    .hasRole(Role.ADMINISTRATOR.name())
//                        .requestMatchers("/account/user/{login}/role/{role}")
//                                 .access(adminAccessWithValidPassword())
//                        .requestMatchers(HttpMethod.PATCH, "/account/user/{login}")
//                                 .access(new WebExpressionAuthorizationManager("#login == authentication.name"))
//                        .requestMatchers(HttpMethod.DELETE, "/account/user/{login}")
//                              .access(new WebExpressionAuthorizationManager("#login == authentication.name or hasRole('ADMINISTRATOR')"))
//                        .requestMatchers(HttpMethod.POST, "/forum/post/{author}")
//                               .access(new WebExpressionAuthorizationManager("#author == authentication.name"))
//                        .requestMatchers(HttpMethod.PATCH, "/forum/post/{id}/comment/{author}")
//                               .access(new WebExpressionAuthorizationManager("#author == authentication.name"))
//                        .requestMatchers(HttpMethod.PATCH, "/forum/post/{id}")
//                                 .access(((authentication, context) ->
//                                new AuthorizationDecision(webSecurity.isPostAuthor(authentication.get().getName(),
//                                        context.getVariables().get("id")))))
//                        .requestMatchers(HttpMethod.DELETE, "/forum/post/{id}")
//                               .access((authentication, context) -> {
//                            boolean isAuthor = webSecurity.isPostAuthor(authentication.get().getName(), context.getVariables().get("id"));
////boolean isModerator = authentication.get().getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("MODERATOR"));
//                            boolean isModerator = context.getRequest().isUserInRole(Role.MODERATOR.name());
//                            return new AuthorizationDecision(isAuthor || isModerator);
//                        })
//                        .anyRequest()
//                              .access(passwordExpiryManager())
//        );
//        return http.build();
//    }
//
//
//    @Bean
//    public PasswordExpiryAuthorizationManager passwordExpiryManager() {
//        return new PasswordExpiryAuthorizationManager(repository, passwordExpiryDays);
//    }
//
//    @Bean
//    public AuthorizationManager<RequestAuthorizationContext> adminAccessWithValidPassword() {
//        return new RoleBasedWithPasswordCheckAuthorizationManager(
//                new WebExpressionAuthorizationManager("hasRole('ADMINISTRATOR')"),
//                passwordExpiryManager()
//        );
//    }
//
//
//}

package telran.java58.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import telran.java58.accounting.dao.UserAccountRepository;
import telran.java58.accounting.model.Role;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final CustomWebSecurity webSecurity;
    private final UserAccountRepository repository;

    @Value("${security.password.expiry-days}")
    private long passwordExpiryDays;

    @Bean
    SecurityFilterChain getSecurityFilterChain(HttpSecurity http) throws Exception {
        http.httpBasic(Customizer.withDefaults());
        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.POST, "/account/register").permitAll()
                .requestMatchers(HttpMethod.PATCH, "/account/password").permitAll()
                .requestMatchers(HttpMethod.GET, "/forum/posts/**").permitAll()

                .requestMatchers("/account/user/{login}/role/{role}")
                .access(adminAccessWithValidPassword())

                .requestMatchers(HttpMethod.PATCH, "/account/user/{login}")
                .access(withPasswordCheck(new WebExpressionAuthorizationManager("#login == authentication.name")))

                .requestMatchers(HttpMethod.DELETE, "/account/user/{login}")
                .access(withPasswordCheck(new WebExpressionAuthorizationManager("#login == authentication.name or hasRole('ADMINISTRATOR')")))

                .requestMatchers(HttpMethod.POST, "/forum/post/{author}")
                .access(withPasswordCheck(new WebExpressionAuthorizationManager("#author == authentication.name")))

                .requestMatchers(HttpMethod.PATCH, "/forum/post/{id}/comment/{author}")
                .access(withPasswordCheck(new WebExpressionAuthorizationManager("#author == authentication.name")))

                .requestMatchers(HttpMethod.PATCH, "/forum/post/{id}")
                .access(withPasswordCheck((authentication, context) ->
                        new AuthorizationDecision(webSecurity.isPostAuthor(
                                authentication.get().getName(),
                                context.getVariables().get("id")))))

                .requestMatchers(HttpMethod.DELETE, "/forum/post/{id}")
                .access(withPasswordCheck((authentication, context) -> {
                    boolean isAuthor = webSecurity.isPostAuthor(authentication.get().getName(), context.getVariables().get("id"));
                    boolean isModerator = context.getRequest().isUserInRole(Role.MODERATOR.name());
                    return new AuthorizationDecision(isAuthor || isModerator);
                }))

                .requestMatchers(HttpMethod.GET, "/account/user/{login}")
                .access(withPasswordCheck(new WebExpressionAuthorizationManager("#login == authentication.name or hasRole('MODERATOR') or hasRole('ADMINISTRATOR')")))

                .anyRequest().access(passwordExpiryManager())
        );

        return http.build();
    }

    @Bean
    public PasswordExpiryAuthorizationManager passwordExpiryManager() {
        return new PasswordExpiryAuthorizationManager(repository, passwordExpiryDays);
    }

    @Bean
    public AuthorizationManager<RequestAuthorizationContext> adminAccessWithValidPassword() {
        return new RoleBasedWithPasswordCheckAuthorizationManager(
                new WebExpressionAuthorizationManager("hasRole('ADMINISTRATOR')"),
                passwordExpiryManager()
        );
    }

    private AuthorizationManager<RequestAuthorizationContext> withPasswordCheck(
            AuthorizationManager<RequestAuthorizationContext> delegate
    ) {
        return new RoleBasedWithPasswordCheckAuthorizationManager(delegate, passwordExpiryManager());
    }
}

