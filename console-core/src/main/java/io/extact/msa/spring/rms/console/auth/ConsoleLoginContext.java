package io.extact.msa.spring.rms.console.auth;

import java.util.Set;

import org.springframework.context.event.EventListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import io.extact.msa.spring.platform.core.auth.anonymous.RmsAnonymousAuthenticationToken;
import io.extact.msa.spring.platform.core.auth.client.RmsClientAuthenticationToken;
import io.extact.msa.spring.platform.core.auth.context.LoginContext;
import io.extact.msa.spring.platform.core.auth.user.RmsAuthentication;

public class ConsoleLoginContext implements LoginContext {

    public void init() {
        RmsAnonymousAuthenticationToken anonymousAuth = RmsAnonymousAuthenticationToken.builder()
                .withCreator(loginUser -> new ConsoleLoginUser(loginUser, "anonymous", "anonymous"))
                .build();
        SecurityContextHolder.setContext(new SecurityContextImpl(anonymousAuth));
    }

    @Override
    public ConsoleLoginUser getLoginUser() {
        RmsAuthentication auth = (RmsAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return (ConsoleLoginUser) auth.getLoginUser();
    }

    // -------------------------------------------------------------- event listener

    @EventListener
    void setRmsAuthenticationToContext(LoggedInEvent event) {

        RmsClientAuthenticationToken clientAuth = RmsClientAuthenticationToken.builder()
                .userId(event.userId())
                .groups(Set.of(event.roleName()))
                .bearerToken(event.bearerToken())
                .loginUserCreator(
                        loginUser -> new ConsoleLoginUser(loginUser, event.loginId(), event.userName()))
                .build();
        SecurityContextHolder.setContext(new SecurityContextImpl(clientAuth));
    }

    @EventListener
    void updateRmsAuthenticationInContext(UserUpdatedEvent event) {

        if (!isLoginUserUpdated(event)) {
            return;
        }

        RmsClientAuthenticationToken oldAuth = (RmsClientAuthenticationToken) SecurityContextHolder
                .getContext()
                .getAuthentication();
        ConsoleLoginUser oldUser = (ConsoleLoginUser) oldAuth.getLoginUser();

        RmsClientAuthenticationToken newAuth = RmsClientAuthenticationToken.builder()
                .userId(event.userId())
                .groups(Set.of(event.roleName()))
                .bearerToken(oldAuth.getBearerTokenCredential().bearToken())
                .loginUserCreator(loginUser -> new ConsoleLoginUser(loginUser, oldUser.getLoginId(), event.userName()))
                .build();
        SecurityContextHolder.setContext(new SecurityContextImpl(newAuth));
    }

    private boolean isLoginUserUpdated(UserUpdatedEvent event) {
        if (!isAuthenticated()) {
            return false;
        }
        return getLoginUser().isSameUserId(event.userId());
    }
}
