package io.extact.msa.spring.rms.console.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.extact.msa.spring.platform.core.auth.context.LoginContext;

@Configuration(proxyBeanMethods = false)
public class ConsoleLoginContextConfig {

    @Bean
    LoginContext loginContext() {
        return new ConsoleLoginContext();
    }
}
