package io.extact.msa.spring.rms.console.service.adapter.local;

import io.extact.msa.spring.platform.fw.domain.event.DomainEventPublisher;
import io.extact.msa.spring.rms.application.universal.LoginService;
import io.extact.msa.spring.rms.console.auth.LoggedInEvent;
import io.extact.msa.spring.rms.console.service.LoginConsoleService;
import io.extact.msa.spring.rms.console.service.model.UserConsoleModel;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Observed(name = "rms-console-local", contextualName = "LoginConsoleService")
public class LocalLoginConsoleService implements LoginConsoleService {

    private final LoginService service;
    private final DomainEventPublisher eventPublisher;

    @Override
    public UserConsoleModel login(String loginId, String password) {

        UserConsoleModel user = service
                .login(loginId, password)
                .transform(ModelConverter::fromUserView);

        LoggedInEvent event = LoggedInEvent.builder()
                .userId(String.valueOf(user.id()))
                .roleName(user.userType().name())
                .bearerToken(LocalLoginConsoleService.class.getSimpleName())
                .loginId(user.loginId())
                .userName(user.userName())
                .build();
        eventPublisher.publish(event);

        return user;
    }
}
