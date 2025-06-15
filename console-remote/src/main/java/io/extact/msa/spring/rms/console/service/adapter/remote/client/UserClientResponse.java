package io.extact.msa.spring.rms.console.service.adapter.remote.client;

import io.extact.msa.spring.rms.console.service.model.ConsoleUserType;
import io.extact.msa.spring.rms.console.service.model.UserConsoleModel;

public record UserClientResponse(
        Integer id,
        String loginId,
        String password,
        String userName,
        String phoneNumber,
        String contact,
        ConsoleUserType userType) {

    public UserConsoleModel toModel() {
        return UserConsoleModel.builder()
                .id(this.id)
                .password(this.password)
                .userType(this.userType)
                .userName(this.userName)
                .phoneNumber(this.phoneNumber)
                .contact(this.contact)
                .build();
    }
}
