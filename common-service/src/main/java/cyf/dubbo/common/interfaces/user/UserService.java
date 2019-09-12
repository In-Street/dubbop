package cyf.dubbo.common.interfaces.user;

import cyf.dubbo.common.model.User;

/**
 * @author Cheng Yufei
 * @create 2019-01-17 14:49
 **/
public interface UserService {

    User getUser();

    User getUserDynamic(User user);

}
