package io.extact.msa.spring.rms.console.service.adapter.remote;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.UriBuilderFactory;

import io.extact.msa.spring.platform.core.auth.client.BearerTokenRequestInitializer;
import io.extact.msa.spring.platform.fw.domain.event.DomainEventPublisher;
import io.extact.msa.spring.platform.fw.feature.event.EventPublisherConfig;
import io.extact.msa.spring.platform.fw.infrastructure.external.CustomUriBuilderFactory;
import io.extact.msa.spring.platform.fw.infrastructure.external.ErrorMessageDeserializer;
import io.extact.msa.spring.platform.fw.infrastructure.external.ExternalProperties;
import io.extact.msa.spring.platform.fw.infrastructure.external.RestClientErrorHandler;
import io.extact.msa.spring.platform.fw.infrastructure.external.converter.ConfigConversionServiceBuilder;
import io.extact.msa.spring.platform.fw.infrastructure.external.converter.ConfigMessageConveterBuilder;
import io.extact.msa.spring.rms.console.service.adapter.remote.client.RmsApplicationClient;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;

@Configuration(proxyBeanMethods = false)
@Import(EventPublisherConfig.class)
public class RemoteServiceConfig {

    @Bean
    @ConfigurationProperties("rms.application")
    ExternalProperties externalProperties() {
        return new ExternalProperties();
    }

    @Bean
    HttpServiceProxyFactory httpServiceProxyFactory(RestClient.Builder builder, ExternalProperties prop, Environment env) {

        ConversionService conversionService = ConfigConversionServiceBuilder
                .builder(prop)
                .build();
        UriBuilderFactory uriFactory = CustomUriBuilderFactory.newInstance()
                .env(env)
                .conversionService(conversionService)
                .uriTemplate(prop.getUrl())
                .build();
        HttpMessageConverter<Object> converter = ConfigMessageConveterBuilder
                .builder(prop)
                .build();

        RestClient restClient = builder
                .uriBuilderFactory(uriFactory)
                .messageConverters(converters -> converters.addFirst(converter))
                .requestInitializer(new BearerTokenRequestInitializer())
                .defaultStatusHandler(new RestClientErrorHandler(new ErrorMessageDeserializer()))
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        return HttpServiceProxyFactory
                .builderFor(adapter)
                .conversionService(conversionService)
                .build();
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
