package com.xkcoding.log.aop;

import com.xkcoding.log.aop.annotation.Log;
import com.xkcoding.log.aop.controller.CCC;
import com.xkcoding.log.aop.controller.TestController;
import com.xkcoding.log.aop.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootDemoLogAopApplicationTests {

    @Autowired
    private TestController testController;

    @Autowired
    CCC ccc;

	@Test
	public void contextLoads() {
	}

	@Test
    public void test03(){
	    User w = new User();
	    w.setId(989999);
	    w.setName("111111111");
        testController.test3(w);
    }

    @Test
    public void test04(){
        User w = new User();
        w.setId(989999);
        w.setName("111111111");
        ccc.test0001(w);
    }

//    @Log(value = "#who.id")
//    //@CachePut(value = "user", key = "#user.id")
//    public String test0001(User who) {
//        if(who == null || who.getName() == null) {
//            return who.getName();
//        } else  {
//            return "none";
//        }
//    }

}
