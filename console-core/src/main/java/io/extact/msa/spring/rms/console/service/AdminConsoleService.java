package io.extact.msa.spring.rms.console.service;

import java.util.List;

import io.extact.msa.spring.rms.console.service.model.ItemConsoleModel;
import io.extact.msa.spring.rms.console.service.model.UserConsoleModel;

public interface AdminConsoleService {

    ItemConsoleModel addItem(ItemConsoleModel addItem);

    List<UserConsoleModel> getAllUsers();

    UserConsoleModel addUser(UserConsoleModel addUser);

    UserConsoleModel updateUser(UserConsoleModel updateUser);
}
