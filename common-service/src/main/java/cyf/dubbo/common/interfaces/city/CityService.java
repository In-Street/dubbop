package cyf.dubbo.common.interfaces.city;

import cyf.dubbo.common.model.City;

/**
 * @author Cheng Yufei
 * @create 2019-01-17 17:42
 **/
public interface CityService {

    String getCityName(String name);

    /**
     * Provider 异步化使用
     *
     * @return
     */
    City asyncProviderCity();
}
