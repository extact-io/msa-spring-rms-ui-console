package io.extact.msa.spring.rms.console.screen;

import io.extact.msa.spring.platform.core.env.MainModuleInformation;
import io.extact.msa.spring.rms.console.auth.ConsoleLoginContext;
import io.extact.msa.spring.rms.console.screen.TransitionMap.Transition;
import io.extact.msa.spring.rms.console.screen.admin.AddItemScreen;
import io.extact.msa.spring.rms.console.screen.admin.AddUserScreen;
import io.extact.msa.spring.rms.console.screen.admin.AdminMainScreen;
import io.extact.msa.spring.rms.console.screen.admin.UpdateUserScreen;
import io.extact.msa.spring.rms.console.screen.login.EndScreen;
import io.extact.msa.spring.rms.console.screen.login.LoginScreen;
import io.extact.msa.spring.rms.console.screen.member.CancelReservationScreen;
import io.extact.msa.spring.rms.console.screen.member.InquiryReservationScreen;
import io.extact.msa.spring.rms.console.screen.member.MemberMainScreen;
import io.extact.msa.spring.rms.console.screen.member.ReserveItemScreen;
import io.extact.msa.spring.rms.console.service.AdminConsoleService;
import io.extact.msa.spring.rms.console.service.LoginConsoleService;
import io.extact.msa.spring.rms.console.service.MemberConsoleService;

/**
 * アプリケーションの画面遷移制御クラス。
 */
public class MainScreenController {

    private final TransitionMap transitionMap;
    private final ConsoleLoginContext loginContext;

    public MainScreenController(
            LoginConsoleService loginService,
            AdminConsoleService adminService,
            MemberConsoleService memberService,
            ConsoleLoginContext loginContext,
            MainModuleInformation moduleInfo) {

        this.transitionMap = new TransitionMap();
        this.loginContext = loginContext;

        transitionMap.add(
                Transition.LOGIN,
                new LoginScreen(loginService, moduleInfo));
        transitionMap.add(
                Transition.MEMBER_MAIN,
                new MemberMainScreen(loginContext));
        transitionMap.add(
                Transition.INQUIRY_RESERVATION,
                new InquiryReservationScreen(memberService, loginContext));
        transitionMap.add(
                Transition.ENTRY_RESERVATRION,
                new ReserveItemScreen(memberService, loginContext));
        transitionMap.add(
                Transition.CANCEL_RESERVATRION,
                new CancelReservationScreen(memberService, loginContext));
        transitionMap.add(
                Transition.ADMIN_MAIN,
                new AdminMainScreen(loginContext));
        transitionMap.add(
                Transition.ENTRY_RENTAL_ITEM,
                new AddItemScreen(adminService, loginContext));
        transitionMap.add(
                Transition.ENTRY_USER,
                new AddUserScreen(adminService, loginContext));
        transitionMap.add(
                Transition.EDIT_USER,
                new UpdateUserScreen(adminService, loginContext));
        transitionMap.add(
                Transition.END,
                new EndScreen());
    }

    public void start() {
        loginContext.init();
        RmsScreen startScreen = transitionMap.stratScreen();
        doPlay(startScreen);
    }

    private RmsScreen doPlay(RmsScreen screen) {
        Transition next = screen.play(true);
        return next != null ? doPlay(transitionMap.nextScreen(next)) : null;
    }
}
