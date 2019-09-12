package cyf.dubbo.api.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fastjson.JSONObject;
//import com.magic.interfaces.service.MagicCityService;
import cyf.dubbo.common.interfaces.city.CityService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Cheng Yufei
 * @create 2019-01-17 17:47
 **/
@RestController
@RequestMapping("/city")
@Slf4j
public class CityController {

    /**
     * 非幂等 设为不重试：retries = -1
     */
    @Reference(version = "1.0.0", group = "CityModule", interfaceClass = CityService.class, async = true, check = false)
//    @Reference(version = "1.0.0", group = "CityModule", interfaceClass = CityService.class)
    private CityService cityService;

    /*@Reference(version = "1.0.0", group = "MagicCityService", interfaceClass = MagicCityService.class,url = "192.168.99.113:20882")
    private MagicCityService magicCityService;*/

    public CityController() {
    }

    /**
     * Consumer 异步
     *
     * @param name
     * @return
     */
    @GetMapping(value = "/getCityNameAsyncConsumer")
    @SneakyThrows({ExecutionException.class, InterruptedException.class})
    public String get(@RequestParam String name) {

        String cityName = cityService.getCityName(name);
        //输出null
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + cityName);
        //async = true, 异步化调用获取结果， 无论Consumer 、Provider 设为异步时
        Future<Object> future = RpcContext.getContext().getFuture();
        cityName = (String) future.get();
        log.debug("result:{}", cityName);

        return cityName;
    }

    /**
     * Provider 异步化使用
     *
     * @return
     */
    @GetMapping("/asyncProvider")
    public String asyncProvider() {

        cityService.asyncProviderCity();
        return "";
    }

    /*@GetMapping("/magic/{id}")
    public JSONObject magicCity(@PathVariable Integer id) {
        return magicCityService.cityHandle(id);
    }*/
}
