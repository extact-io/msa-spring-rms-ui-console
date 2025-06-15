package io.extact.msa.spring.rms.console.screen.admin;

import static io.extact.msa.spring.rms.console.screen.ClientConstants.*;

import java.util.List;

import io.extact.msa.spring.platform.fw.exception.BusinessFlowException;
import io.extact.msa.spring.rms.console.auth.ConsoleLoginContext;
import io.extact.msa.spring.rms.console.screen.RmsScreen;
import io.extact.msa.spring.rms.console.screen.TransitionMap.Transition;
import io.extact.msa.spring.rms.console.screen.textio.RmsStringInputReader.PatternMessage;
import io.extact.msa.spring.rms.console.screen.textio.TextIoUtils;
import io.extact.msa.spring.rms.console.service.AdminConsoleService;
import io.extact.msa.spring.rms.console.service.model.ConsoleUserType;
import io.extact.msa.spring.rms.console.service.model.UserConsoleModel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateUserScreen implements RmsScreen {

    private final AdminConsoleService service;
    private final ConsoleLoginContext loginContext;

    @Override
    public Transition play(boolean printHeader) {

        if (printHeader) {
            TextIoUtils.printScreenHeader(loginContext.getLoginUser(), "ユーザ情報編集画面");
        }

        // ユーザ一覧を表示
        List<UserConsoleModel> users = service.getAllUsers();
        TextIoUtils.println(EDIT_USER_INFORMATION);
        users.forEach(dto -> TextIoUtils.println(USER_FORMAT.format(dto)));
        TextIoUtils.blankLine();

        // 編集するユーザを選択
        int selectId = TextIoUtils.newIntInputReader()
                .withSelectableValues(users
                        .stream()
                        .map(UserConsoleModel::id)
                        .toList(),
                        SCREEN_BREAK_VALUE)
                .read("ユーザ番号");
        if (TextIoUtils.isBreak(selectId)) {
            return Transition.ADMIN_MAIN;
        }
        TextIoUtils.blankLine();

        UserConsoleModel targetUser = users.stream()
                .filter(user -> user.id() == selectId)
                .findFirst()
                .get();

        // パスワードの入力
        String password = TextIoUtils.newStringInputReader()
                .withDefaultValue(targetUser.password())
                .withMinLength(5)
                .withMaxLength(15)
                .read("パスワード");

        // ユーザ名の入力
        String userName = TextIoUtils.newStringInputReader()
                .withDefaultValue(targetUser.userName())
                .withMinLength(1)
                .read("ユーザ名");

        // 電話番号の入力
        String phoneNumber = TextIoUtils.newStringInputReader()
                .withDefaultValue(targetUser.phoneNumber())
                .withMaxLength(14)
                .withPattern(PatternMessage.PHONE_NUMBER)
                .read("電話番号（省略可）");

        // 連絡先の入力
        String contact = TextIoUtils.newStringInputReader()
                .withDefaultValue(targetUser.contact())
                .withMaxLength(15)
                .read("連絡先（省略可）");

        // 会員種別の入力
        ConsoleUserType userType = TextIoUtils.newEnumInputReader(ConsoleUserType.class)
                .withDefaultValue(targetUser.userType())
                .read("権限");

        UserConsoleModel command = UserConsoleModel.builder()
                .password(password)
                .userName(userName)
                .phoneNumber(phoneNumber)
                .contact(contact)
                .userType(userType)
                .build();

        // ユーザ情報の更新実行
        try {
            UserConsoleModel updatedUser = service.updateUser(command);
            this.printResultInformation(updatedUser);
            return Transition.ADMIN_MAIN;

        } catch (BusinessFlowException e) {
            TextIoUtils.printServerError(e);
            return play(false); // start over!!

        }
    }

    private void printResultInformation(UserConsoleModel updatedUser) {
        TextIoUtils.blankLine();
        TextIoUtils.println("***** ユーザ登録結果 *****");
        TextIoUtils.printf("[%s]のユーザ情報を更新しました", updatedUser.id());
        TextIoUtils.blankLine();
        TextIoUtils.blankLine();
        TextIoUtils.waitPressEnter();
    }
}
