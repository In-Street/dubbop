package cyf.dubbo.api.controller;

import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.service.GenericService;
import cyf.dubbo.common.interfaces.user.UserService;
import cyf.dubbo.common.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Cheng Yufei
 * @create 2019-01-17 16:34
 **/
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    /**
     * 自定义ReferenceConfig 实现接口调用
     */
    @Autowired
    private ReferenceConfig referenceConfig;

    /**
     * 基于接口的远程调用
     */
   /* @Reference(version = "1.0.0", group = "UserModule", timeout = 3000, interfaceClass = UserService.class*//*, async = true*//*)
    private UserService userService;*/



    /**
     * ServiceConfig、ReferenceConfig等编程接口采集的配置（配置获取方式一种，另可通过.properties文件获取配置）
     * 泛化调用：【无需引用 @Reference】
     * 1.在没有引入公共接口的module （没有接口及模型类）
     * 2. 可通过 GenericService 调用所有服务实现，而不需要依赖服务接口
     */
    @GetMapping("/getUserGeneric")
    public Object getGeneric() throws ExecutionException, InterruptedException {
        //泛型参数设置为GenericService
        ReferenceConfig<GenericService> referenceConfig = new ReferenceConfig<GenericService>();
        referenceConfig.setInterface("cyf.dubbo.common.interfaces.user.UserService");
        referenceConfig.setGroup("UserModule");
        referenceConfig.setVersion("1.0.0");
        //设置为泛化
        referenceConfig.setGeneric(true);
//        referenceConfig.setAsync(true);

        GenericService service = referenceConfig.get();
//        Object result = service.$invoke("getUser", new String[0], new Object[0]);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", 100);
        map.put("name", "Candice");
        map.put("city", "北京");
        Object result = service.$invoke("getUser", new String[]{"cyf.dubbo.common.model.User"}, new Object[]{map});
        // setAsync 时通过此方式获取值
       /* Future<Object> future = RpcContext.getContext().getFuture();
        return future.get();*/
        return result;

    }

    /**
     * 1. async = true【全部方法都为异步】：Consumer 异步
     *
     * 2. 定义 ReferenceConfig ,设置具体异步方法，而不是全部
     *
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("/getUserAsyncConsumer")
    public Object getUserAsync() throws ExecutionException, InterruptedException, IllegalAccessException, InstantiationException {
        UserService userService = (UserService) referenceConfig.get();
        //方式一
        User user = userService.getUserDynamic(new User(100,"Candice","Bei Jing"));
        //此时输出为null
        System.out.println(user);
        Future<Object> future = RpcContext.getContext().getFuture();
        User result = (User) future.get();
        //请求线程
        log.info("getUser 执行：{}", Thread.currentThread().getName());
        return result;

        //方式二
       /* Future<Object> future = RpcContext.getContext().asyncCall(() -> {
            //请求线程
            log.info("getUser 执行：{}", Thread.currentThread().getName());
            return userService.getUser();
        });
        return future.get();*/
    }

    @GetMapping("/getUserSyncConsumer")
    public Object getUserSync() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

        UserService userService = (UserService)referenceConfig.get();
        User user = userService.getUser();
        System.out.println(user);
        return user;
    }


}
