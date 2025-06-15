package io.extact.msa.spring.rms.console.screen.member;

import static io.extact.msa.spring.rms.console.screen.ClientConstants.*;

import java.time.LocalDate;
import java.util.List;

import io.extact.msa.spring.rms.console.auth.ConsoleLoginContext;
import io.extact.msa.spring.rms.console.screen.RmsScreen;
import io.extact.msa.spring.rms.console.screen.TransitionMap.Transition;
import io.extact.msa.spring.rms.console.screen.textio.TextIoUtils;
import io.extact.msa.spring.rms.console.service.MemberConsoleService;
import io.extact.msa.spring.rms.console.service.model.ItemConsoleModel;
import io.extact.msa.spring.rms.console.service.model.MemberReservationConsoleModel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InquiryReservationScreen implements RmsScreen {

    private final MemberConsoleService service;
    private final ConsoleLoginContext loginContext;

    @Override
    public Transition play(boolean printHeader) {

        if (printHeader) {
            TextIoUtils.printScreenHeader(loginContext.getLoginUser(), "予約照会画面");
        }

        // レンタル品一覧を表示
        List<ItemConsoleModel> items = service.getAllItems();
        TextIoUtils.println(INQUIRY_RESERVATION_INFORMATION);
        items.forEach(dto -> TextIoUtils.println(ITEM_FORMAT.format(dto)));
        TextIoUtils.blankLine();

        // 照会するレンタル品を選択
        int selectedItemId = TextIoUtils.newIntInputReader()
                .withSelectableValues(
                        items.stream()
                                .map(ItemConsoleModel::id)
                                .toList(),
                        SCREEN_BREAK_VALUE)
                .read("レンタル品番号");
        if (TextIoUtils.isBreak(selectedItemId)) {
            return Transition.MEMBER_MAIN;
        }

        // 照会する日付を入力
        LocalDate inputedDate = TextIoUtils.newLocalDateReader()
                .read("日付（入力例－2020/10/23）");

        // 照会の実行
        List<MemberReservationConsoleModel> results = service.findReservationByItemIdAndFromDate(selectedItemId, inputedDate);

        // 該当データなしの場合は最初から
        if (results.isEmpty()) {
            TextIoUtils.printErrorInformation(DATA_NOT_FOUND_INFORMATION);
            return play(false); // start over!!
        }
        // 該当データありの場合は結果出力してメインメニューへ
        this.printResultList(selectedItemId, inputedDate, results);
        return Transition.MEMBER_MAIN;
    }

    private void printResultList(int selectedItem, LocalDate inputedDate, List<MemberReservationConsoleModel> reservations) {
        TextIoUtils.blankLine();
        TextIoUtils.println("***** 予約検索結果 *****");
        TextIoUtils.println("選択レンタル品番号：" + selectedItem);
        TextIoUtils.println("入力日付：" + DATE_FORMAT.format(inputedDate));
        reservations.forEach(r -> TextIoUtils.println(RESERVATION_FORMAT.format(r)));
        TextIoUtils.blankLine();
        TextIoUtils.waitPressEnter();
    }
}
