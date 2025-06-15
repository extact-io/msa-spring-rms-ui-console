package io.extact.msa.spring.rms.console.service.adapter.remote.client;

import io.extact.msa.spring.rms.console.service.model.ConsoleUserType;
import io.extact.msa.spring.rms.console.service.model.UserConsoleModel;

public record UserAddClientRequest(
        String loginId,
        String password,
        ConsoleUserType userType,
        String userName,
        String phoneNumber,
        String contact) {

    public static UserAddClientRequest from(UserConsoleModel model) {
        return new UserAddClientRequest(
                model.loginId(),
                model.password(),
                model.userType(),
                model.userName(),
                model.phoneNumber(),
                model.contact());
    }
}
