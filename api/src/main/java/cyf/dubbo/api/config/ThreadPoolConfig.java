package cyf.dubbo.api.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Cheng Yufei
 * @create 2018-09-27 15:43
 **/
@Configuration
public class ThreadPoolConfig {

    @Bean(name = "threadPool")
    public ThreadPoolExecutor executor() {
        ThreadFactory build = new ThreadFactoryBuilder().setNameFormat("demo_pool_%d").build();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(8, 30,
                60, TimeUnit.SECONDS,
                //LinkedBlockingQueue：链表存储数据，有两把锁，放数据锁与取数据锁，放入数据的线程和取出数据的线程可以同时操作；不指定容器大小时为Integer.MAX_VALUE
                //ArrayBlockingQueue：放数据线程与取数据线程是互斥的，需指定容器大小
                new LinkedBlockingQueue<>(100), build, new ThreadPoolExecutor.CallerRunsPolicy());
        return threadPoolExecutor;
    }
}
