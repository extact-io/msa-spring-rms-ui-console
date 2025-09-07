package io.extact.msa.spring.rms.console.screen.admin;

import static io.extact.msa.spring.rms.console.screen.ClientConstants.*;

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
public class AddUserScreen implements RmsScreen {

    private final AdminConsoleService service;
    private final ConsoleLoginContext loginContext;

    @Override
    public Transition play(boolean printHeader) {

        if (printHeader) {
            TextIoUtils.printScreenHeader(loginContext.getLoginUser(), "ユーザ登録画面");
        }

        // 入力インフォメーションの表示
        TextIoUtils.println(ENTRY_USER_INFORMATION);

        // ログインIDの入力
        String loginId = TextIoUtils.newStringInputReader()
                .withMinLength(5)
                .withMaxLength(15)
                .withExcludeCheckString(SCREEN_BREAK_KEY)
                .read("ログインID");
        if (TextIoUtils.isBreak(loginId)) {
            return Transition.ADMIN_MAIN;
        }

        // パスワードの入力
        String password = TextIoUtils.newStringInputReader()
                .withMinLength(5)
                .withMaxLength(15)
                .read("パスワード");

        // ユーザ名の入力
        String userName = TextIoUtils.newStringInputReader()
                .withMinLength(1)
                .read("ユーザ名");

        // 電話番号の入力
        String phoneNumber = TextIoUtils.newStringInputReader()
                .withMaxLength(14)
                .withPattern(PatternMessage.PHONE_NUMBER)
                .withDefaultValue("")
                .read("電話番号（省略可）");

        // 連絡先の入力
        String contact = TextIoUtils.newStringInputReader()
                .withMaxLength(15)
                .withDefaultValue("")
                .read("連絡先（省略可）");

        // 会員種別の入力
        ConsoleUserType userType = TextIoUtils.newEnumInputReader(ConsoleUserType.class)
                .withDefaultValue(ConsoleUserType.MEMBER)
                .read("権限");

        UserConsoleModel command = UserConsoleModel.builder()
                .loginId(loginId)
                .password(password)
                .userName(userName)
                .phoneNumber(phoneNumber)
                .contact(contact)
                .userType(userType)
                .build();

        // ユーザ登録の実行
        try {
            UserConsoleModel newUser = service.addUser(command);
            this.printResultInformation(newUser);
            return Transition.ADMIN_MAIN;

        } catch (BusinessFlowException e) {
            TextIoUtils.printServerError(e);
            return play(false); // start over!!

        }
    }

    private void printResultInformation(UserConsoleModel newUser) {
        TextIoUtils.blankLine();
        TextIoUtils.println("***** ユーザ登録結果 *****");
        TextIoUtils.println("ユーザ番号：" + newUser.id());
        TextIoUtils.println("ログインID：" + newUser.loginId());
        TextIoUtils.println("パスワード：" + newUser.password());
        TextIoUtils.println("ユーザ名：" + newUser.userName());
        TextIoUtils.println("電話番号：" + newUser.phoneNumber());
        TextIoUtils.println("連絡先：" + newUser.contact());
        TextIoUtils.println("権限：" + newUser.userType().name());
        TextIoUtils.blankLine();
        TextIoUtils.waitPressEnter();
    }
}
