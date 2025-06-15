package io.extact.msa.spring.rms.console;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.extact.msa.spring.platform.core.CoreConfig;
import io.extact.msa.spring.rms.application.ApplicationServiceConfig;
import io.extact.msa.spring.rms.console.auth.ConsoleLoginContextConfig;
import io.extact.msa.spring.rms.console.service.adapter.local.LocalServiceConfig;
import io.extact.msa.spring.rms.domain.DomainConfig;
import io.extact.msa.spring.rms.infrastructure.persistence.PersistenceConfig;

// localの場合はwebapiの@SpringBootConfigurationがクラスパスに
// 含まれるため@Configurationを使っている
@Configuration
@EnableAutoConfiguration
@Import({
        CoreConfig.class,
        ConsoleLoginContextConfig.class,
        MainScreenConfig.class,
        LocalServiceConfig.class,
        ApplicationServiceConfig.class,
        DomainConfig.class,
        PersistenceConfig.class })
public class LocalConsoleApplication {

    public static void main(String[] args) throws Exception {

        new SpringApplicationBuilder()
                /*
                 * 読み込む設定ファイル名を起動時に変更する
                 *  - application-local.ymlが優先されrms-application.jarのapplication.ymlは
                 *    読み込まれなくなる
                 */
                .properties("spring.config.name:application-local")
                .sources(LocalConsoleApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}
