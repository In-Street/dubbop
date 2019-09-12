package cyf.dubbo.provider;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubboConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(scanBasePackages = {"cyf.dubbo.provider"})
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableDubboConfig
public class ProviderCityApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ProviderCityApplication.class).run(args);
    }
}