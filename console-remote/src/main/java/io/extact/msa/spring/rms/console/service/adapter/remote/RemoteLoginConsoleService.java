package io.extact.msa.spring.rms.console.service.adapter.remote;

import org.springframework.http.ResponseEntity;

import io.extact.msa.spring.platform.core.auth.client.BearerTokenExtractor;
import io.extact.msa.spring.platform.fw.domain.event.DomainEventPublisher;
import io.extact.msa.spring.rms.console.auth.LoggedInEvent;
import io.extact.msa.spring.rms.console.service.LoginConsoleService;
import io.extact.msa.spring.rms.console.service.adapter.remote.client.LoginClientRequest;
import io.extact.msa.spring.rms.console.service.adapter.remote.client.RmsApplicationClient;
import io.extact.msa.spring.rms.console.service.adapter.remote.client.UserClientResponse;
import io.extact.msa.spring.rms.console.service.model.UserConsoleModel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RemoteLoginConsoleService implements LoginConsoleService {

    private final RmsApplicationClient client;
    private final DomainEventPublisher eventPublisher;

    @Override
    public UserConsoleModel login(String loginId, String password) {

        LoginClientRequest request = LoginClientRequest.builder()
                .loginId(loginId)
                .password(password)
                .build();

        ResponseEntity<UserClientResponse> response = client.login(request);

        UserClientResponse user = response.getBody();
        String bearerToken = BearerTokenExtractor.extract(response.getHeaders());

        LoggedInEvent event = LoggedInEvent.builder()
                .userId(String.valueOf(user.id()))
                .roleName(user.userType().name())
                .bearerToken(bearerToken)
                .loginId(user.loginId())
                .userName(user.userName())
                .build();
        eventPublisher.publish(event);

        return user.toModel();
    }
}
