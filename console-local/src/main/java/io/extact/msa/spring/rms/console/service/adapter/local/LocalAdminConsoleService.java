package io.extact.msa.spring.rms.console.service.adapter.local;

import java.util.List;

import io.extact.msa.spring.platform.fw.domain.event.DomainEventPublisher;
import io.extact.msa.spring.rms.application.admin.ItemAddCommand;
import io.extact.msa.spring.rms.application.admin.ItemAdminService;
import io.extact.msa.spring.rms.application.admin.UserAddCommand;
import io.extact.msa.spring.rms.application.admin.UserAdminService;
import io.extact.msa.spring.rms.application.admin.UserUpdateCommand;
import io.extact.msa.spring.rms.console.auth.UserUpdatedEvent;
import io.extact.msa.spring.rms.console.service.AdminConsoleService;
import io.extact.msa.spring.rms.console.service.model.ItemConsoleModel;
import io.extact.msa.spring.rms.console.service.model.UserConsoleModel;
import io.extact.msa.spring.rms.domain.user.model.UserModelView;
import io.extact.msa.spring.rms.domain.user.model.UserType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LocalAdminConsoleService implements AdminConsoleService {

    private final ItemAdminService itemService;
    private final UserAdminService userService;
    private final DomainEventPublisher eventPublisher;


    @Override
    public ItemConsoleModel addItem(ItemConsoleModel item) {

        ItemAddCommand command = ItemAddCommand.builder()
                .serialNo(item.serialNo())
                .itemName(item.itemName())
                .build();
        return itemService.add(command).transform(ModelConverter::fromItemView);
    }

    @Override
    public List<UserConsoleModel> getAllUsers() {
        return userService
                .getAll()
                .stream()
                .map(ModelConverter::fromUserView)
                .toList();
    }

    @Override
    public UserConsoleModel addUser(UserConsoleModel user) {
        UserAddCommand command = UserAddCommand.builder()
                .loginId(user.loginId())
                .password(user.password())
                .userName(user.userName())
                .phoneNumber(user.phoneNumber())
                .contact(user.contact())
                .userType(UserType.valueOf(user.userType().name()))
                .build();
        return userService.add(command).transform(ModelConverter::fromUserView);
    }

    @Override
    public UserConsoleModel updateUser(UserConsoleModel model) {

        UserUpdateCommand command = UserUpdateCommand.builder()
                .password(model.password())
                .userName(model.userName())
                .phoneNumber(model.phoneNumber())
                .contact(model.contact())
                .userType(UserType.valueOf(model.userType().name()))
                .build();

        UserModelView updatedView = userService.update(command);

        UserUpdatedEvent event = UserUpdatedEvent.builder()
                .userId(String.valueOf(updatedView.getId().id()))
                .roleName(updatedView.getUserType().name())
                .userName(updatedView.getProfile().getUserName())
                .build();
        eventPublisher.publish(event);

        return updatedView.transform(ModelConverter::fromUserView);
    }
}
