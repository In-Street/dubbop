package cyf.dubbo.provider;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubboConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(scanBasePackages = {"cyf.dubbo.provider"})
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableDubboConfig
public class ProviderUserApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ProviderUserApplication.class).run(args);
    }
}