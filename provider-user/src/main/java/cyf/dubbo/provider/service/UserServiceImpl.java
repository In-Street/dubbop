package cyf.dubbo.provider.service;

import com.alibaba.dubbo.config.annotation.Service;
import cyf.dubbo.common.interfaces.user.UserService;
import cyf.dubbo.common.model.User;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Cheng Yufei
 * @create 2019-01-17 14:56
 **/
@Service(interfaceClass = UserService.class, group = "UserModule", version = "1.0.0")
@Slf4j
public class UserServiceImpl implements UserService {

    @Override
    public User getUser() {
        User user = new User();
        user.setId(1);
        user.setName("Taylor");
        // 默认业务线程池执行
        log.info("User 执行：{}", Thread.currentThread().getName());
        return user;
    }

    @Override
    public User getUserDynamic(User user) {
        log.info("User 执行：{}", Thread.currentThread().getName());
        return user;
    }
}
