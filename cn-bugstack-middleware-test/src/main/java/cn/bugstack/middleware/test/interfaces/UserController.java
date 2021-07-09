package cn.bugstack.middleware.test.interfaces;

import cn.bugstack.middleware.monitor.annotation.DoMonitor;
import cn.bugstack.middleware.test.interfaces.dto.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * 测试：http://localhost:8081/api/queryUserInfo?userId=aaa
     */
    @DoMonitor(key = "cn.bugstack.middleware.UserController.queryUserInfo", desc = "查询用户信息")
    @RequestMapping(path = "/api/queryUserInfo", method = RequestMethod.GET)
    public UserInfo queryUserInfo(@RequestParam String userId) {
        logger.info("查询用户信息，userId：{}", userId);
        return new UserInfo("虫虫:" + userId, 19, "天津市东丽区万科赏溪苑14-0000");
    }

}
