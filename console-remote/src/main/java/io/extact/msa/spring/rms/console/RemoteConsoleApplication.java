package io.extact.msa.spring.rms.console;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;

import io.extact.msa.spring.platform.core.CoreConfig;
import io.extact.msa.spring.rms.console.auth.ConsoleLoginContextConfig;
import io.extact.msa.spring.rms.console.service.adapter.remote.RemoteServiceConfig;

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
                .web(WebApplicationType.NONE)
                .run(args);
    }
}
