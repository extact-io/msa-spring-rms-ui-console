package io.extact.msa.spring.rms.console.screen.member;

import static io.extact.msa.spring.rms.console.screen.ClientConstants.*;

import java.util.List;

import io.extact.msa.spring.platform.fw.exception.BusinessFlowException;
import io.extact.msa.spring.rms.console.auth.ConsoleLoginContext;
import io.extact.msa.spring.rms.console.screen.ClientConstants;
import io.extact.msa.spring.rms.console.screen.RmsScreen;
import io.extact.msa.spring.rms.console.screen.TransitionMap.Transition;
import io.extact.msa.spring.rms.console.screen.textio.TextIoUtils;
import io.extact.msa.spring.rms.console.service.MemberConsoleService;
import io.extact.msa.spring.rms.console.service.model.MemberReservationConsoleModel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CancelReservationScreen implements RmsScreen {

    private final MemberConsoleService service;
    private final ConsoleLoginContext loginContext;

    @Override
    public Transition play(boolean printHeader) {

        if (printHeader) {
            TextIoUtils.printScreenHeader(loginContext.getLoginUser(), "レンタル品予約キャンセル画面");
        }

        List<MemberReservationConsoleModel> ownReservations = service.getOwnReservations();

        // キャンセル可能な予約がない場合はメニューへ戻る
        if (ownReservations.isEmpty()) {
            TextIoUtils.printWarningInformation(CANNOT_CANCEL_RESERVATION_INFORMATION);
            TextIoUtils.waitPressEnter();
            return Transition.MEMBER_MAIN;
        }

        // キャンセル可能な予約一覧を表示
        TextIoUtils.println(CANCEL_RESERVATION_INFORMATION);
        ownReservations.forEach(r -> TextIoUtils.println(ClientConstants.RESERVATION_FORMAT.format(r)));
        TextIoUtils.blankLine();

        // キャンセルする予約を選択
        int selectedReservationId = TextIoUtils.newIntInputReader()
                .withSelectableValues(
                        ownReservations.stream()
                                .map(MemberReservationConsoleModel::id)
                                .toList(),
                        SCREEN_BREAK_VALUE)
                .read("予約番号");
        if (TextIoUtils.isBreak(selectedReservationId)) {
            return Transition.MEMBER_MAIN;
        }

        TextIoUtils.blankLine();

        // レンタル品の予約キャンセルの実行
        try {
            service.cancelReservation(selectedReservationId);
            this.printResultInformation(selectedReservationId);
            return Transition.MEMBER_MAIN;

        } catch (BusinessFlowException e) {
            TextIoUtils.printServerError(e);
            return play(false); // start over!!

        }
    }

    private void printResultInformation(Integer selectedItem) {
        TextIoUtils.println("***** 予約キャンセル確定結果 *****");
        TextIoUtils.printf("[%s]の予約をキャンセルしました", selectedItem);
        TextIoUtils.blankLine();
        TextIoUtils.blankLine();
        TextIoUtils.waitPressEnter();
    }
}
