package io.extact.msa.spring.rms.console.auth;

import java.util.Set;

import io.extact.msa.spring.platform.core.auth.user.LoginUser;
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
    public int getUserId() {
        return platformLoginUser.getUserId();
    }

    @Override
    public Set<String> getGroups() {
        return platformLoginUser.getGroups();
    }
}
