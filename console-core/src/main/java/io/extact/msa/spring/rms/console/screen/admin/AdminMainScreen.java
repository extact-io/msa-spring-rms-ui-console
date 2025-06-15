package io.extact.msa.spring.rms.console.screen.admin;

import io.extact.msa.spring.rms.console.auth.ConsoleLoginContext;
import io.extact.msa.spring.rms.console.screen.RmsScreen;
import io.extact.msa.spring.rms.console.screen.TransitionMap.Transition;
import io.extact.msa.spring.rms.console.screen.textio.TextIoUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AdminMainScreen implements RmsScreen {

    @RequiredArgsConstructor
    public enum AdminMenuList {

        ENTRY_RENTAL_ITEM("レンタル品登録", Transition.ENTRY_RENTAL_ITEM),
        ENTRY_USER("ユーザ登録", Transition.ENTRY_USER),
        EDIT_USER("ユーザ編集", Transition.EDIT_USER),
        RELOGIN("再ログイン", Transition.LOGIN),
        END("終了", Transition.END);

        private final String name;
        private final Transition transition;

        Transition getTransition() {
            return transition;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private final ConsoleLoginContext loginContext;

    @Override
    public Transition play(boolean printHeader) {

        TextIoUtils.printScreenHeader(loginContext.getLoginUser(), "管理者サービスメニュー画面");

        AdminMenuList selectedMenu = TextIoUtils
                .newEnumInputReader(AdminMenuList.class)
                .read("メニュー番号を入力して下さい。");

        return selectedMenu.getTransition();
    }
}
