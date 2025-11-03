package io.extact.msa.spring.rms.console.service.adapter.remote;

import java.util.List;

import io.extact.msa.spring.platform.fw.domain.event.DomainEventPublisher;
import io.extact.msa.spring.rms.console.auth.UserUpdatedEvent;
import io.extact.msa.spring.rms.console.service.AdminConsoleService;
import io.extact.msa.spring.rms.console.service.adapter.remote.client.ItemAddClientRequest;
import io.extact.msa.spring.rms.console.service.adapter.remote.client.RmsApplicationClient;
import io.extact.msa.spring.rms.console.service.adapter.remote.client.UserAddClientRequest;
import io.extact.msa.spring.rms.console.service.adapter.remote.client.UserClientResponse;
import io.extact.msa.spring.rms.console.service.adapter.remote.client.UserUpdateClientRequest;
import io.extact.msa.spring.rms.console.service.model.ItemConsoleModel;
import io.extact.msa.spring.rms.console.service.model.UserConsoleModel;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Observed(name = "rms-console-remote")
public class RemoteAdminConsoleService implements AdminConsoleService {

    private final RmsApplicationClient client;
    private final DomainEventPublisher eventPublisher;

    @Override
    public ItemConsoleModel addItem(ItemConsoleModel item) {
        return client
                .addItem(item.transform(ItemAddClientRequest::from))
                .toModel();
    }

    @Override
    public List<UserConsoleModel> getAllUsers() {
        return client.getAllUsers().stream()
                .map(UserClientResponse::toModel)
                .toList();
    }

    @Override
    public UserConsoleModel addUser(UserConsoleModel user) {
        return client
                .addUser(user.transform(UserAddClientRequest::from))
                .toModel();
    }

    @Override
    public UserConsoleModel updateUser(UserConsoleModel model) {

        UserConsoleModel updatedUser = client
                .updateUser(model.transform(UserUpdateClientRequest::from))
                .toModel();

        UserUpdatedEvent event = UserUpdatedEvent.builder()
                .userId(String.valueOf(updatedUser.id()))
                .roleName(updatedUser.userType().name())
                .userName(updatedUser.userName())
                .build();
        eventPublisher.publish(event);

        return updatedUser;
    }
}
