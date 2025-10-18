package io.extact.msa.spring.rms.console;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import io.extact.msa.spring.platform.core.CoreConfig;
import io.extact.msa.spring.rms.console.auth.ConsoleLoginContextConfig;
import io.extact.msa.spring.rms.console.service.adapter.remote.RemoteServiceConfig;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
@AutoConfigureObservability // SpringBootTestの機能でOFFされるので手動ONにする
class RemoteClientScenarioTest extends AbstractClientScenarioTest {

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    @Import({
            TestcontainersConfig.class,
            CoreConfig.class,
            ConsoleLoginContextConfig.class,
            RemoteServiceConfig.class })
    static class TestConfig {
    }
}
