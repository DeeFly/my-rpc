package info.gaofei;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit test for simple App.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/application-context.xml")
public class AppTest {
    @Autowired
    private RpcProxy rpcProxy;

    @Test
    public void helloTest() {
        Service helloService = rpcProxy.create(Service.class);
        String result = helloService.sayHello("World");
        Assert.assertEquals("helloWorld", result);
    }
}
