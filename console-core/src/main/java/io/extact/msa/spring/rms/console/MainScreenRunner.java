package io.extact.msa.spring.rms.console;

import static io.extact.msa.spring.rms.console.screen.ClientConstants.*;

import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import io.extact.msa.spring.platform.core.env.ActiveProfileResolver;
import io.extact.msa.spring.platform.core.env.MainModuleInformation;
import io.extact.msa.spring.platform.core.env.StartupLog;
import io.extact.msa.spring.platform.fw.feature.exception.RmsServiceUnavailableException;
import io.extact.msa.spring.rms.console.screen.MainScreenController;
import io.extact.msa.spring.rms.console.screen.textio.TextIoUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * コンソールアプリの実質的なエントリポイント。
 */
@Slf4j
public class MainScreenRunner implements CommandLineRunner, ApplicationContextAware {

    private static final String START_UP_LOGO = """
                ____    __  ___  _____
               / __ \\  /  |/  / / ___/
              / /_/ / / /|_/ /  \\__ \\
             / _, _/ / /  / /_ ___/ /
            /_/ |_(_)_/  /_/(_)____(_)
            """;
    private final MainScreenController mainController;
    private ApplicationContext context;

    public MainScreenRunner(
            MainScreenController mainController,
            MainModuleInformation info,
            ActiveProfileResolver profileResolver) {

        this.mainController = mainController;

        StartupLog.startupLog(info, profileResolver);
        TextIoUtils.println(START_UP_LOGO);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Override
    public void run(String... args) throws Exception {

        while (true) {
            try {
                mainController.start();
                break;
            } catch (RmsServiceUnavailableException e) {
                log.warn(e.getMessage());
                TextIoUtils.printErrorInformation(SERVICE_UNAVAILABLE_INFORMATION);
            } catch (Exception e) {
                log.error("Back to start..", e);
                TextIoUtils.printErrorInformation(UNKNOWN_ERROR_INFORMATION);
            }
        }

        int exitCode = SpringApplication.exit(context, () -> 0);
        System.exit(exitCode); // main threadが残るためexitする
    }
}
