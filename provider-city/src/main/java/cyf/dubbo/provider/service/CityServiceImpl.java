package cyf.dubbo.provider.service;

import com.alibaba.dubbo.config.annotation.Service;
import cyf.dubbo.common.interfaces.city.CityService;
import cyf.dubbo.common.model.City;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Cheng Yufei
 * @create 2019-01-17 17:44
 **/
@Service(interfaceClass = CityService.class, group = "CityModule", version = "1.0.0", timeout = 6000/*, async = true*/)
@Slf4j
public class CityServiceImpl implements CityService {

    @Override
    public String getCityName(String name) {
        // City 执行：DubboServerHandler-192.168.1.191:20881-thread-9
        log.info("City 执行：{}，入参：{}", Thread.currentThread().getName(),name);
        return "City: " + name;
    }

    /**
     * Provider 异步化使用
     *
     * @return
     */
    @Override
    public City asyncProviderCity() {
        try {
            Thread.sleep(2000);
            // City Async:DubboServerHandler-192.168.1.181:20881-thread-2
            log.info("City Async:{}", Thread.currentThread().getName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new City(300, "new york");
    }
}

/**
 * async = true ，consumer无需等待provider执行完成，
 * 不添加异步时，consumer 一直等待 provider 完成执行。
 *
 * 1.服务提供方，线程调度模式默认：all,
 *      provider执行线程：DubboServerHandler-192.168.1.181:20881-thread-2 ;
 *
 * 2. 调度模式：direct
 *    provider执行线程：  New I/O server worker #1-1
 *
 * 3. 调度模式：message / execution
 *  ， provider执行线程： DubboServerHandler-192.168.1.181:20881-thread-1
 */
