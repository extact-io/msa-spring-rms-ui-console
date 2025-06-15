package io.extact.msa.spring.rms.console.screen.member;

import static io.extact.msa.spring.rms.console.screen.ClientConstants.*;

import java.time.LocalDateTime;
import java.util.List;

import io.extact.msa.spring.platform.fw.exception.BusinessFlowException;
import io.extact.msa.spring.rms.console.auth.ConsoleLoginContext;
import io.extact.msa.spring.rms.console.screen.RmsScreen;
import io.extact.msa.spring.rms.console.screen.TransitionMap.Transition;
import io.extact.msa.spring.rms.console.screen.textio.TextIoUtils;
import io.extact.msa.spring.rms.console.service.MemberConsoleService;
import io.extact.msa.spring.rms.console.service.model.ItemConsoleModel;
import io.extact.msa.spring.rms.console.service.model.MemberReservationConsoleModel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReserveItemScreen implements RmsScreen {

    private final MemberConsoleService service;
    private final ConsoleLoginContext loginContext;

    @Override
    public Transition play(boolean printHeader) {

        if (printHeader) {
            TextIoUtils.printScreenHeader(loginContext.getLoginUser(), "レンタル品予約画面");
        }

        // レンタル品一覧を表示
        List<ItemConsoleModel> items = service.getAllItems();
        TextIoUtils.println(ENTRY_RESERVATION_INFORMATION);
        items.forEach(dto ->
        TextIoUtils.println(ITEM_FORMAT.format(dto))
        );
        TextIoUtils.blankLine();

        // 予約するレンタル品の選択
        int selectedItem = TextIoUtils.newIntInputReader()
                .withSelectableValues(
                        items.stream().map(ItemConsoleModel::id).toList(),
                        SCREEN_BREAK_VALUE)
                .read("レンタル品番号");
        if (TextIoUtils.isBreak(selectedItem)) {
            return Transition.MEMBER_MAIN;
        }

        // 利用開始日時の入力
        LocalDateTime fromDateTime = TextIoUtils.newLocalDateTimeReader()
                .withFutureNow()
                .read("利用開始日時（入力例－2020/04/01 09:00）:");

        // 利用終了日時の入力
        LocalDateTime toDateTime = TextIoUtils.newLocalDateTimeReader()
                .withFutureThan(fromDateTime)
                .read("利用終了日時（入力例－2020/04/01 18:00）:");

        // 備考の入力
        String note = TextIoUtils.newStringInputReader()
                .withMaxLength(15)
                .withDefaultValue("")
                .read("備考（空白可）");

        TextIoUtils.blankLine();

        MemberReservationConsoleModel command = MemberReservationConsoleModel.builder()
                .fromDateTime(fromDateTime)
                .toDateTime(toDateTime)
                .note(note)
                .itemId(selectedItem)
                .build();

        // レンタル品予約の実行
        try {
            MemberReservationConsoleModel newReservation = service.reserveItem(command);
            this.printResultInformation(newReservation);
            return Transition.MEMBER_MAIN;

        } catch (BusinessFlowException e) {
            TextIoUtils.printServerError(e);
            return play(false); // start over!!

        }
    }

    private void printResultInformation(MemberReservationConsoleModel newReservation) {
        TextIoUtils.println("***** 予約確定結果 *****");
        TextIoUtils.printf(RESERVATION_FORMAT.format(newReservation));
        TextIoUtils.blankLine();
        TextIoUtils.blankLine();
        TextIoUtils.waitPressEnter();
    }
}
