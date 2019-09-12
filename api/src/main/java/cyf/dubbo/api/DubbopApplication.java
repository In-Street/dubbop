package cyf.dubbo.api;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubboConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author zhuruisong on 2018/4/24
 * @since 1.0
 */
@SpringBootApplication(scanBasePackages = {"cyf.dubbo.api"})
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableDubboConfig
public class DubbopApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(DubbopApplication.class).run(args);
    }
}