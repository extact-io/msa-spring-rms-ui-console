package io.extact.msa.spring.rms.console.auth;

import java.util.Set;

import io.extact.msa.spring.platform.core.auth.user.AuthUserId;
import io.extact.msa.spring.platform.core.auth.user.LoginUser;
import io.extact.msa.spring.platform.core.auth.user.UserAttributes;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConsoleLoginUser implements LoginUser {

    private final LoginUser platformLoginUser;
    @Getter
    private final String loginId;
    @Getter
    private final String userName;

    @Override
    public AuthUserId getUserId() {
        return platformLoginUser.getUserId();
    }

    @Override
    public Set<String> getGroups() {
        return platformLoginUser.getGroups();
    }

    @Override // AuthenticatedPrincipal#getName()
    public String getName() {
        return String.valueOf(getUserId().value());
    }

    @Override
    public <T extends UserAttributes> T getAttributes(Class<T> clazz) {
        throw new UnsupportedOperationException();
    }
}
