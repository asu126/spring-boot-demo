package com.xkcoding.log.aop.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.xkcoding.log.aop.annotation.Log;
import com.xkcoding.log.aop.domain.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 测试 Controller
 * </p>
 *
 * @package: com.xkcoding.log.aop.controller
 * @description: 测试 Controller
 * @author: yangkai.shen
 * @date: Created in 2018/10/1 10:10 PM
 * @copyright: Copyright (c) 2018
 * @version: V1.0
 * @modified: yangkai.shen
 */
@RestController
public class TestController {

	/**
	 * 测试方法
	 *
	 * @param who 测试参数
	 * @return {@link Dict}
	 */
	@GetMapping("/test")
    @Log("执行方法二")
	public Dict test(String who) {
		return Dict.create().set("who", StrUtil.isBlank(who) ? "me" : who);
	}


    @Log(value = "#who.id")
    //@CachePut(value = "user", key = "#user.id")
    public String test3(User who) {
        if(who == null || who.getName() == null) {
            return who.getName();
        } else  {
            return "none";
        }
    }

}
