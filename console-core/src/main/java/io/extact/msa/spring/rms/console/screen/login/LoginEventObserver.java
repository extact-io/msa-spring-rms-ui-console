package io.extact.msa.spring.rms.console.screen.login;

import io.extact.msa.spring.rms.console.service.model.UserConsoleModel;

public interface LoginEventObserver {

    void onEvent(UserConsoleModel loginUser);

    UserConsoleModel getLoginUser();
}
