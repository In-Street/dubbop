package cyf.dubbo.api.config;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.MethodConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import cyf.dubbo.common.interfaces.user.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 消费者配置
 *
 * @author Cheng Yufei
 * @create 2019-03-07 16:31
 **/
@Configuration
public class ReferenceConfiguration {

    private static ReferenceConfig<UserService> referenceConfig ;

    static {
        referenceConfig = new ReferenceConfig<UserService>();
        referenceConfig.setInterface(UserService.class);
        referenceConfig.setVersion("1.0.0");
        referenceConfig.setGroup("UserModule");
        referenceConfig.setApplication(new ApplicationConfig("api"));
        referenceConfig.setRegistry(new RegistryConfig("zookeeper://39.106.118.71:2181"));
    }

    private static final List<MethodConfig> asyncMethods = new ArrayList<>();

    @Bean
    public ReferenceConfig asyncMethod() {

        MethodConfig methodConfig = new MethodConfig();
        methodConfig.setName("getUserDynamic");
        methodConfig.setAsync(true);
        asyncMethods.add(methodConfig);
        referenceConfig.setMethods(asyncMethods);
        return referenceConfig;
    }
}
