package io.extact.msa.spring.rms.console;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import io.extact.msa.spring.platform.core.CoreConfig;
import io.extact.msa.spring.platform.fw.exception.BusinessFlowException;
import io.extact.msa.spring.platform.fw.exception.BusinessFlowException.CauseType;
import io.extact.msa.spring.rms.console.auth.ConsoleLoginContextConfig;
import io.extact.msa.spring.rms.console.service.AdminConsoleService;
import io.extact.msa.spring.rms.console.service.LoginConsoleService;
import io.extact.msa.spring.rms.console.service.MemberConsoleService;
import io.extact.msa.spring.rms.console.service.adapter.remote.RemoteServiceConfig;
import io.extact.msa.spring.rms.console.service.model.ConsoleUserType;
import io.extact.msa.spring.rms.console.service.model.ItemConsoleModel;
import io.extact.msa.spring.rms.console.service.model.MemberReservationConsoleModel;
import io.extact.msa.spring.rms.console.service.model.UserConsoleModel;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
@AutoConfigureObservability
class ClientScenarioTest {

    @Autowired
    private LoginConsoleService loginService;
    @Autowired
    private AdminConsoleService adminService;
    @Autowired
    private MemberConsoleService memberService;

    private static ScenarioContext context = new ScenarioContext();

    static class ScenarioContext {
        private UserConsoleModel memberUser;
        private ItemConsoleModel registeredItem;
    }

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    @Import({
            TestcontainersConfig.class,
            CoreConfig.class,
            ConsoleLoginContextConfig.class,
            RemoteServiceConfig.class })
    static class TestConfig {
    }

    @Test
    @Order(1)
    void addUserAccountScenario() {

        // -----------------------------------
        // ユーザの登録
        // -----------------------------------
        // ★:ADMINから開始
        loginService.login("admin", "admin");

        UserConsoleModel newUser = UserConsoleModel.builder()
                .loginId("testUser")
                .password("testUser")
                .userName("テスト太郎")
                .phoneNumber("1234")
                .contact("なぞなぞ部門")
                .userType(ConsoleUserType.MEMBER)
                .build();

        UserConsoleModel addedUser = adminService.addUser(newUser);

        // 登録を依頼した内容と登録された内容が同じか？
        assertThat(addedUser).isNotNull();
        assertThat(addedUser.id()).isNotNull().isGreaterThan(3); // 採番済みの3より大きい番号が採番されていること
        assertThat(addedUser.loginId()).isEqualTo(newUser.loginId());
        assertThat(addedUser.password()).isEqualTo(newUser.password());
        assertThat(addedUser.userName()).isEqualTo(newUser.userName());
        assertThat(addedUser.phoneNumber()).isEqualTo(newUser.phoneNumber());
        assertThat(addedUser.contact()).isEqualTo(newUser.contact());
        assertThat(addedUser.userType()).isEqualTo(newUser.userType());


        // -----------------------------------
        // 登録したユーザでログインできるか
        // -----------------------------------
        // ★:MEMBERに切り替え
        UserConsoleModel loginUser = loginService.login(addedUser.loginId(), addedUser.password());

        // 取得したログインユーザの情報が登録したユーザの情報と同じであること
        assertThat(loginUser).isNotNull();
        assertThat(loginUser).isEqualTo(addedUser);

        // -----------------------------------
        // 登録したユーザを再登録（重複エラー）
        // -----------------------------------
        // ★:ADMINに切り替え
        loginService.login("admin", "admin");

        // 名前が重複
        UserConsoleModel duplicateUser = UserConsoleModel.builder()
                .loginId(newUser.loginId())
                .password(newUser.password())
                .userName(newUser.userName())
                .phoneNumber(newUser.phoneNumber())
                .contact(newUser.contact())
                .userType(newUser.userType())
                .build();

        BusinessFlowException thrown = assertThrows(BusinessFlowException.class, () -> {
            adminService.addUser(duplicateUser);
        });
        assertThat(thrown.getCauseType()).isEqualTo(CauseType.DUPLICATE);

        // @Order(3)の事前条件として設定
        context.memberUser = loginUser;
    }

    @Test
    @Order(2)
    void addRentalScenario() {

        // -----------------------------------
        // レンタル品の登録
        // -----------------------------------
        ItemConsoleModel newItem = ItemConsoleModel.builder()
                .serialNo("1234")
                .itemName("レンタル品")
                .build();

        ItemConsoleModel addedItem = adminService.addItem(newItem);

        // 登録を依頼した内容と登録された内容が同じか？
        assertThat(addedItem).isNotNull();
        assertThat(addedItem.id()).isNotNull().isGreaterThan(4); // 採番済みの4より大きい番号が採番されていること
        assertThat(addedItem.serialNo()).isEqualTo(newItem.serialNo());
        assertThat(addedItem.itemName()).isEqualTo(newItem.itemName());


        // -----------------------------------
        // 登録したレンタル品を再登録（重複エラー）
        // -----------------------------------
        // シリアルNoが重複
        ItemConsoleModel duplicateItem = ItemConsoleModel.builder()
                .serialNo(newItem.serialNo())
                .itemName(newItem.itemName())
                .build();

        BusinessFlowException thrown = assertThrows(BusinessFlowException.class, () -> {
            adminService.addItem(duplicateItem);
        });
        assertThat(thrown.getCauseType()).isEqualTo(CauseType.DUPLICATE);

        // @Order(3)の事前条件として設定
        context.registeredItem = addedItem;
    }

    @Test
    @Order(3)
    void reserveScenario() {

        // -----------------------------------
        // @Order(1)で登録したログインユーザと@Order(2)で登録したレンタル品を予約できるか
        // -----------------------------------

        // 事前条件の取得
        UserConsoleModel loginUser = context.memberUser;
        ItemConsoleModel reservationTargetItem = context.registeredItem;

        // ★:MEMBERに切り替え
        loginService.login(loginUser.loginId(), loginUser.password());

        // 登録したレンタル品がレンタル品一覧に含まれていること
        List<ItemConsoleModel> items = memberService.getAllItems();
        assertThat(items).contains(reservationTargetItem);

        // 登録したユーザでレンタル品を予約
        LocalDateTime fromDateTime = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime toDateTime = fromDateTime.plusDays(1);
        String note = "備考";

        MemberReservationConsoleModel reserveRequest = MemberReservationConsoleModel.builder()
                .fromDateTime(fromDateTime)
                .toDateTime(toDateTime)
                .note(note)
                .itemId(reservationTargetItem.id())
                .build();

        MemberReservationConsoleModel reserved = memberService.reserveItem(reserveRequest);

        // 依頼した予約内容と同じか？
        assertThat(reserved).isNotNull();
        assertThat(reserved.id()).isNotNull().isGreaterThan(3); // // 採番済みの3より大きい番号が採番されていること
        assertThat(reserved.fromDateTime()).isEqualTo(reserveRequest.fromDateTime());
        assertThat(reserved.toDateTime()).isEqualTo(reserveRequest.toDateTime());
        assertThat(reserved.note()).isEqualTo(reserveRequest.note());
        assertThat(reserved.itemId()).isEqualTo(reservationTargetItem.id());
        assertThat(reserved.serialNo()).isEqualTo(reservationTargetItem.serialNo());
        assertThat(reserved.itemName()).isEqualTo(reservationTargetItem.itemName());
        assertThat(reserved.reserverId()).isEqualTo(loginUser.id());


        // -----------------------------------
        // 予約照会で登録した予約を参照できるか
        // -----------------------------------
        List<MemberReservationConsoleModel> reservations = memberService
                .findReservationByItemIdAndFromDate(reserved.itemId(), reserved.fromDateTime().toLocalDate());

        // 該当の予約が1件であること
        assertThat(reservations).hasSize(1);
        // 該当の予約が登録した予約と同じであること
        assertThat(reservations.get(0)).isEqualTo(reserved);
    }

    @Test
    @Order(4)
    void reserveOnNotExistsScenario() {

        // -----------------------------------
        // 誤ったレンタル品IDで登録
        // -----------------------------------

        LocalDateTime fromDateTime = LocalDateTime.now().plusDays(10).truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime toDateTime = fromDateTime.plusDays(1);
        String note = "備考";
        MemberReservationConsoleModel reserveRequest = MemberReservationConsoleModel.builder()
                .fromDateTime(fromDateTime)
                .toDateTime(toDateTime)
                .note(note)
                .itemId(999) //存在しないID
                .build();

        BusinessFlowException thrown = assertThrows(BusinessFlowException.class, () -> {
            memberService.reserveItem(reserveRequest);
        });
        assertThat(thrown.getCauseType()).isEqualTo(CauseType.NOT_FOUND);
    }

    @Test
    @Order(5)
    void cancelReservationScenario() {

        // -----------------------------------
        // @Order(3)で登録した予約をキャンセルできるか
        // -----------------------------------

        // ログインユーザの予約一覧を取得
        List<MemberReservationConsoleModel> ownReservations = memberService.getOwnReservations();
        // 該当の予約が1件であること
        assertThat(ownReservations).hasSize(1);

        // ログインユーザ以外の予約を削除
        BusinessFlowException thrown = assertThrows(BusinessFlowException.class, () -> {
            memberService.cancelReservation(1);
        });
        assertThat(thrown.getCauseType()).isEqualTo(CauseType.FORBIDDEN);

        // キャンセル対象の予約を取得
        MemberReservationConsoleModel cancelTarget = ownReservations.get(0);
        // キャンセルの実行
        memberService.cancelReservation(cancelTarget.id());

        // 再度ログインユーザの予約一覧を取得
        ownReservations = memberService.getOwnReservations();
        // 自分の予約一覧に出てなこないこと
        assertThat(ownReservations).isEmpty();
    }

    @Test
    @Order(6)
    void updateUserAccountScenario() {

        // -----------------------------------
        // ユーザ情報を更新できるか
        // -----------------------------------
        // ★:ADMINに切り替え
        loginService.login("admin", "admin");

        // 一覧を取得
        List<UserConsoleModel> users = adminService.getAllUsers();
        assertThat(users).hasSize(4);

        // 一覧から更新対象を選択
        UserConsoleModel updateTarget = users.get(0);
        // ユーザ名を変更
        UserConsoleModel updateRequest = UserConsoleModel.builder()
                .id(updateTarget.id())
                .loginId(updateTarget.loginId())
                .password(updateTarget.password())
                .userName("UPDATE")
                .contact(updateTarget.contact())
                .phoneNumber(updateTarget.phoneNumber())
                .userType(updateTarget.userType())
                .build();

        // 更新の実行
        UserConsoleModel actual = adminService.updateUser(updateRequest);
        assertThat(actual).isEqualTo(updateRequest);
    }
}
