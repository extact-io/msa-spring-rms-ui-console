package io.extact.msa.spring.rms.console.service.adapter.remote.client;

import io.extact.msa.spring.rms.console.service.model.ConsoleUserType;
import io.extact.msa.spring.rms.console.service.model.UserConsoleModel;

public record UserUpdateClientRequest(
        Integer id,
        String password,
        ConsoleUserType userType,
        String userName,
        String phoneNumber,
        String contact) {

    public static UserUpdateClientRequest from(UserConsoleModel model) {
        return new UserUpdateClientRequest(
                model.id(),
                model.password(),
                model.userType(),
                model.userName(),
                model.phoneNumber(),
                model.contact());
    }
}
