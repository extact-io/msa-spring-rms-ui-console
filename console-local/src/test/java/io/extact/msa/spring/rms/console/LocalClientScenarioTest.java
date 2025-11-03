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
import io.extact.msa.spring.platform.fw.feature.observation.ObservationConfig;
import io.extact.msa.spring.rms.application.ApplicationServiceConfig;
import io.extact.msa.spring.rms.console.auth.ConsoleLoginContextConfig;
import io.extact.msa.spring.rms.console.service.adapter.local.LocalServiceConfig;
import io.extact.msa.spring.rms.domain.DomainConfig;
import io.extact.msa.spring.rms.infrastructure.persistence.PersistenceConfig;

@SpringBootTest(webEnvironment = WebEnvironment.NONE, args = "spring.config.name=application-local")
@ActiveProfiles({ "jpa-all", "test" })
@TestMethodOrder(OrderAnnotation.class)
@AutoConfigureObservability
class LocalClientScenarioTest extends AbstractClientScenarioTest {

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    @Import({
            CoreConfig.class,
            ConsoleLoginContextConfig.class,
            ObservationConfig.class,
            LocalServiceConfig.class,
            ApplicationServiceConfig.class,
            DomainConfig.class,
            PersistenceConfig.class })
    static class TestConfig {
    }
}
