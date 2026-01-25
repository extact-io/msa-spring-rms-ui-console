package io.extact.msa.spring.rms.console.service.adapter.remote;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import io.extact.msa.spring.platform.fw.domain.service.DomainEventPublisher;
import io.extact.msa.spring.platform.fw.feature.event.EventPublisherConfig;
import io.extact.msa.spring.platform.fw.infrastructure.external.ExternalProperties;
import io.extact.msa.spring.platform.fw.infrastructure.external.customizer.BearerTokenRequestInitializerCustomizer;
import io.extact.msa.spring.platform.fw.infrastructure.external.customizer.RmsRestClientCustomizer;
import io.extact.msa.spring.platform.fw.infrastructure.external.customizer.SingleRestClientConfig;
import io.extact.msa.spring.rms.console.service.adapter.remote.client.RmsApplicationClient;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;

@Configuration(proxyBeanMethods = false)
@Import({
        SingleRestClientConfig.class,
        EventPublisherConfig.class })
public class RemoteServiceConfig {

    @Bean
    @ConfigurationProperties("rms.application")
    ExternalProperties externalProperties() {
        return new ExternalProperties();
    }

    @Bean
    RmsRestClientCustomizer overrideRestClientConfig() {
        return BearerTokenRequestInitializerCustomizer.INSTANCE;
    }

    @Bean
    RmsApplicationClient rmsApplicationClient(HttpServiceProxyFactory factory) {
        return factory.createClient(RmsApplicationClient.class);
    }

    @Bean
    RemoteAdminConsoleService remoteAdminConsoleService(
            RmsApplicationClient client,
            DomainEventPublisher eventPublisher) {

        return new RemoteAdminConsoleService(client, eventPublisher);
    }

    @Bean
    RemoteLoginConsoleService remoteLoginConsoleService(
            RmsApplicationClient client,
            DomainEventPublisher eventPublisher) {

        return new RemoteLoginConsoleService(client, eventPublisher);
    }

    @Bean
    RemoteMemberConsoleService remoteMemberConsoleService(
            RmsApplicationClient client,
            DomainEventPublisher eventPublisher) {

        return new RemoteMemberConsoleService(client);
    }

    @Bean
    @ConditionalOnClass(ObservedAspect.class)
    @ConditionalOnProperty(name = "env.otlp.enabled", havingValue = "true")
    ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
        return new ObservedAspect(observationRegistry);
    }
}
