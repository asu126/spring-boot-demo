package com.xkcoding.log.aop.controller;

import com.xkcoding.log.aop.annotation.Log;
import com.xkcoding.log.aop.domain.User;
import org.springframework.stereotype.Component;


@Component
public class CCC {

    @Log(value = "#who.id")
    //@CachePut(value = "user", key = "#user.id")
    public String test0001(User who) {
        if(who == null || who.getName() == null) {
            return who.getName();
        } else  {
            return "none";
        }
    }
}
