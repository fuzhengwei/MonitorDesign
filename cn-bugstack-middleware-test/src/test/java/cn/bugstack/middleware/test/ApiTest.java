package cn.bugstack.middleware.test;

import cn.bugstack.middleware.test.interfaces.UserController;
import cn.bugstack.middleware.test.interfaces.dto.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiTest {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    public static void main(String[] args) {
        new ApiTest().queryUserInfo("aaa");
    }

    public UserInfo queryUserInfo(String userId) {
        logger.info("查询用户信息，userId：{}", userId);
        return new UserInfo("虫虫:" + userId, 19, "天津市东丽区万科赏溪苑14-0000");
    }

}
