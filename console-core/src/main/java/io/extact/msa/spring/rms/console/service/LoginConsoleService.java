package io.extact.msa.spring.rms.console.service;

import io.extact.msa.spring.rms.console.service.model.UserConsoleModel;

public interface LoginConsoleService {
    UserConsoleModel login(String loginId, String password);
}
