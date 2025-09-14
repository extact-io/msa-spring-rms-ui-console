package io.extact.msa.spring.rms.console;

import jakarta.annotation.PostConstruct;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import io.extact.msa.spring.platform.core.CoreConfig;
import io.extact.msa.spring.rms.console.auth.ConsoleLoginContextConfig;
import io.extact.msa.spring.rms.console.service.adapter.remote.RemoteServiceConfig;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;

/**
 * リモートコンソールアプリケーション。
 * デフォルトのTerminal実装はEclipseから起動するときに標準入力がうまく動作しなくなるため
 * <code>-Dorg.beryx.textio.TextTerminal=org.beryx.textio.console.ConsoleTextTerminal</code>
 * を引数に指定する。
 */
@SpringBootConfiguration
@EnableAutoConfiguration
@Import({
        CoreConfig.class,
        ConsoleLoginContextConfig.class,
        MainScreenConfig.class,
        RemoteServiceConfig.class })
public class RemoteConsoleApplication {

    public static void main(String[] args) throws Exception {
        new SpringApplicationBuilder()
                .sources(RemoteConsoleApplication.class)
                //.web(WebApplicationType.NONE) // for actuator
                .run(args);
    }

    // TODO: Condationalにしてfwに持っていく
    @Bean
    OpenTelemetryAppenderInitializer openTelemetryAppenderInitializer(OpenTelemetry openTelemetry) {
        return new OpenTelemetryAppenderInitializer(openTelemetry);
    }

    @Bean
    ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
        return new ObservedAspect(observationRegistry);
    }

    static class OpenTelemetryAppenderInitializer {

        private final OpenTelemetry openTelemetry;

        OpenTelemetryAppenderInitializer(OpenTelemetry openTelemetry) {
            this.openTelemetry = openTelemetry;
        }

        @PostConstruct
        void init() {
            OpenTelemetryAppender.install(this.openTelemetry);
        }

    }
}
