package io.extact.msa.spring.rms.console;

import java.io.IOException;
import java.time.Duration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import lombok.extern.slf4j.Slf4j;

@TestConfiguration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "rms.test.use-testcontainer", havingValue = "true")
@Slf4j
class TestcontainersConfig {

    @Bean
    @SuppressWarnings("resource")
    ComposeContainer composeContainer(Environment env) throws IOException {
        return new ComposeContainer(new ClassPathResource("compose.yml").getFile())
                // コンテナ内の rms-application-1 の 8080ポートをホスト側に公開する
                // 実際にはホスト側の空きポートに動的にマッピングされる
                .withExposedService("rms-application-1", 8080) // コンテナ側のサービス名とポート
                .waitingFor("rms-application-1", Wait.forHttp("/actuator/health/readiness")
                        .withStartupTimeout(Duration.ofSeconds(10)))
                .withEnv("RMS_LOG_SERVER_ENABLE", "true");
    }

    @Bean
    DynamicPropertyRegistrar remoteUrlRegistrar(ComposeContainer container, Environment env) {
        // コンテナ内の rms-application-1 の 8080ポートに対するホスト側のホスト名とポート
        // 通常、localhost:32782 のようになる
        String host = container.getServiceHost("rms-application-1", 8080);
        int port = container.getServicePort("rms-application-1", 8080);
        String destination = "http://" + host + ":" + port;
        log.info("DESTINATION -> " + destination);
        return registry -> registry.add("rms.application.url", () -> destination);
    }
}
