package io.extact.msa.spring.rms.console;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
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
import io.extact.msa.spring.rms.console.service.model.UserConsoleModel;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
public class CientScenarioTest {

    @Autowired
    private LoginConsoleService loginService;
    @Autowired
    private AdminConsoleService adminService;
    @Autowired
    private MemberConsoleService memberService;

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
    void testAddUserAccount() {

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
        assertThat(addedUser.id()).isNotNull();
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
    }
}
