package io.extact.msa.spring.rms.console.service.adapter.local;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.extact.msa.spring.platform.fw.domain.service.DomainEventPublisher;
import io.extact.msa.spring.platform.fw.feature.event.EventPublisherConfig;
import io.extact.msa.spring.rms.application.admin.ItemAdminService;
import io.extact.msa.spring.rms.application.admin.UserAdminService;
import io.extact.msa.spring.rms.application.member.ReserveItemService;
import io.extact.msa.spring.rms.application.universal.LoginService;
import io.extact.msa.spring.rms.console.service.AdminConsoleService;
import io.extact.msa.spring.rms.console.service.LoginConsoleService;
import io.extact.msa.spring.rms.console.service.MemberConsoleService;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;

@Configuration(proxyBeanMethods = false)
@Import(EventPublisherConfig.class)
public class LocalServiceConfig {

    @Bean
    AdminConsoleService localAdminConsoleService(
            ItemAdminService itemService,
            UserAdminService userService,
            DomainEventPublisher eventPublisher) {

        return new LocalAdminConsoleService(itemService, userService, eventPublisher);
    }

    @Bean
    LoginConsoleService localLoginConsoleService(LoginService service, DomainEventPublisher eventPublisher) {
        return new LocalLoginConsoleService(service, eventPublisher);
    }

    @Bean
    MemberConsoleService localMemberConsoleService(ReserveItemService service) {
        return new LocalMemberConsoleService(service);
    }

    @Bean
    @ConditionalOnClass(ObservedAspect.class)
    @ConditionalOnProperty(name = "env.otlp.enabled", havingValue = "true")
    ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
        return new ObservedAspect(observationRegistry);
    }
}
