package io.extact.msa.spring.rms.console.service.model;

import io.extact.msa.spring.platform.core.generic.Transformable;
import lombok.Builder;

@Builder
public record UserConsoleModel(
        Integer id,
        String loginId,
        String password,
        String userName,
        String phoneNumber,
        String contact,
        ConsoleUserType userType) implements Transformable {
}
