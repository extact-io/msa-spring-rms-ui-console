package io.extact.msa.spring.rms.console;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.extact.msa.spring.platform.core.env.ActiveProfileResolver;
import io.extact.msa.spring.platform.core.env.MainModuleInformation;
import io.extact.msa.spring.rms.console.auth.ConsoleLoginContext;
import io.extact.msa.spring.rms.console.screen.MainScreenController;
import io.extact.msa.spring.rms.console.service.AdminConsoleService;
import io.extact.msa.spring.rms.console.service.LoginConsoleService;
import io.extact.msa.spring.rms.console.service.MemberConsoleService;

@Configuration(proxyBeanMethods = false)
public class MainScreenConfig {

    @Bean
    MainScreenController mainScreenController(
            LoginConsoleService loginService,
            AdminConsoleService adminService,
            MemberConsoleService memberService,
            ConsoleLoginContext loginContext,
            MainModuleInformation moduleInfo) {

        return new MainScreenController(
                loginService,
                adminService,
                memberService,
                loginContext,
                moduleInfo);
    }

    @Bean
    MainScreenRunner mainScreenRunner(
            MainScreenController controller,
            MainModuleInformation info,
            ActiveProfileResolver profileResolver) {

        return new MainScreenRunner(controller, info, profileResolver);
    }
}
