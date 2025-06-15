package io.extact.msa.spring.rms.console.screen.admin;

import static io.extact.msa.spring.rms.console.screen.ClientConstants.*;

import io.extact.msa.spring.platform.fw.exception.BusinessFlowException;
import io.extact.msa.spring.rms.console.auth.ConsoleLoginContext;
import io.extact.msa.spring.rms.console.screen.RmsScreen;
import io.extact.msa.spring.rms.console.screen.TransitionMap.Transition;
import io.extact.msa.spring.rms.console.screen.textio.RmsStringInputReader.PatternMessage;
import io.extact.msa.spring.rms.console.screen.textio.TextIoUtils;
import io.extact.msa.spring.rms.console.service.AdminConsoleService;
import io.extact.msa.spring.rms.console.service.model.ItemConsoleModel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddItemScreen implements RmsScreen {

    private final AdminConsoleService service;
    private final ConsoleLoginContext loginContext;

    @Override
    public Transition play(boolean printHeader) {

        if (printHeader) {
            TextIoUtils.printScreenHeader(loginContext.getLoginUser(), "レンタル品登録画面");
        }

        // 入力インフォメーションの表示
        TextIoUtils.println(ENTRY_RENTAL_ITEM_INFORMATION);

        // シリアル番号の入力
        String serialNo = TextIoUtils.newStringInputReader()
                .withMinLength(1)
                .withMaxLength(15)
                .withPattern(PatternMessage.SERIAL_NO)
                .read("シリアル番号");
        if (TextIoUtils.isBreak(serialNo)) {
            return Transition.ADMIN_MAIN;
        }

        // 品名の入力
        String itemName = TextIoUtils.newStringInputReader()
                .withMaxLength(15)
                .withDefaultValue("")
                .read("品名（空白可）");

        TextIoUtils.blankLine();

        ItemConsoleModel command = ItemConsoleModel.builder()
                .serialNo(serialNo)
                .itemName(itemName)
                .build();

        // レンタル品登録の実行
        try {
            ItemConsoleModel newItem = service.addItem(command);
            this.printResultInformation(newItem);
            return Transition.ADMIN_MAIN;

        } catch (BusinessFlowException e) {
            TextIoUtils.printServerError(e);
            return play(false); // start over!!

        }
    }

    private void printResultInformation(ItemConsoleModel newItem) {
        TextIoUtils.println("***** レンタル品登録結果 *****");
        TextIoUtils.printf(ITEM_FORMAT.format(newItem));
        TextIoUtils.blankLine();
        TextIoUtils.waitPressEnter();
    }
}
